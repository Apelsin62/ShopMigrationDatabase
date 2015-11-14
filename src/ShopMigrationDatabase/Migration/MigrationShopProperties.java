/*
 * НЕ ИЗМЕНЯТЬ И НЕ УДАЛЯТЬ АВТОРСКИЕ ПРАВА И ЗАГОЛОВОК ФАЙЛА
 * 
 * Копирайт © 2010-2016, CompuProject и/или дочерние компании.
 * Все права защищены.
 * 
 * ShopMigrationDatabase это программное обеспечение предоставленное и разработанное 
 * CompuProject в рамках проекта ApelsinShop без каких либо сторонних изменений.
 * 
 * Распространение, использование исходного кода в любой форме и/или его 
 * модификация разрешается при условии, что выполняются следующие условия:
 * 
 * 1. При распространении исходного кода должно оставатсья указанное выше 
 *    уведомление об авторских правах, этот список условий и последующий 
 *    отказ от гарантий.
 * 
 * 2. При изменении исходного кода должно оставатсья указанное выше 
 *    уведомление об авторских правах, этот список условий, последующий 
 *    отказ от гарантий и пометка о сделанных изменениях.
 * 
 * 3. Распространение и/или изменение исходного кода должно происходить
 *    на условиях Стандартной общественной лицензии GNU в том виде, в каком 
 *    она была опубликована Фондом свободного программного обеспечения;
 *    либо лицензии версии 3, либо (по вашему выбору) любой более поздней
 *    версии. Вы должны были получить копию Стандартной общественной 
 *    лицензии GNU вместе с этой программой. Если это не так, см. 
 *    <http://www.gnu.org/licenses/>.
 * 
 * ShopMigrationDatabase распространяется в надежде, что она будет полезной,
 * но БЕЗО ВСЯКИХ ГАРАНТИЙ; даже без неявной гарантии ТОВАРНОГО ВИДА
 * или ПРИГОДНОСТИ ДЛЯ ОПРЕДЕЛЕННЫХ ЦЕЛЕЙ. Подробнее см. в Стандартной
 * общественной лицензии GNU.
 * 
 * НИ ПРИ КАКИХ УСЛОВИЯХ ПРОЕКТ, ЕГО УЧАСТНИКИ ИЛИ CompuProject НЕ 
 * НЕСУТ ОТВЕТСТВЕННОСТИ ЗА КАКИЕ ЛИБО ПРЯМЫЕ, КОСВЕННЫЕ, СЛУЧАЙНЫЕ, 
 * ОСОБЫЕ, ШТРАФНЫЕ ИЛИ КАКИЕ ЛИБО ДРУГИЕ УБЫТКИ (ВКЛЮЧАЯ, НО НЕ 
 * ОГРАНИЧИВАЯСЬ ПРИОБРЕТЕНИЕМ ИЛИ ЗАМЕНОЙ ТОВАРОВ И УСЛУГ; ПОТЕРЕЙ 
 * ДАННЫХ ИЛИ ПРИБЫЛИ; ПРИОСТАНОВЛЕНИЕ БИЗНЕСА). 
 * 
 * ИСПОЛЬЗОВАНИЕ ДАННОГО ИСХОДНОГО КОДА ОЗНАЧАЕТ, ЧТО ВЫ БЫЛИ ОЗНАКОЛМЛЕНЫ
 * СО ВСЕМИ ПРАВАМИ, СТАНДАРТАМИ И УСЛОВИЯМИ, УКАЗАННЫМИ ВЫШЕ, СОГЛАСНЫ С НИМИ
 * И ОБЯЗУЕТЕСЬ ИХ СОБЛЮДАТЬ.
 * 
 * ЕСЛИ ВЫ НЕ СОГЛАСНЫ С ВЫШЕУКАЗАННЫМИ ПРАВАМИ, СТАНДАРТАМИ И УСЛОВИЯМИ, 
 * ТО ВЫ МОЖЕТЕ ОТКАЗАТЬСЯ ОТ ИСПОЛЬЗОВАНИЯ ДАННОГО ИСХОДНОГО КОДА.
 * 
 */
package ShopMigrationDatabase.Migration;

import ShopMigrationDatabase.Helpers.MigrationHelper;
import ShopMigrationDatabase.Helpers.MySQLHelper;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Maxim Zaytsev
 */
public class MigrationShopProperties {

    private final MySQLHelper oldFormatDB;
    private final MySQLHelper newFormatDB;
    private final PreparedStatement updatePreparedStatement;
    private final PreparedStatement insertPreparedStatement;
    private final Map<String, String> aliasId = new HashMap<>();
    private final Map<String, String> allNames = new HashMap<>();

    public MigrationShopProperties(MySQLHelper oldFormatDB, MySQLHelper newFormatDB) {
        this.oldFormatDB = oldFormatDB;
        this.newFormatDB = newFormatDB;
        this.updatePreparedStatement = this.newFormatDB.preparedStatement(
                "UPDATE `ShopProperties` SET `propertyName`=?, `filterType`=?, "
                + "`valueType`=?, `oneOfAllValues`=? WHERE `id`=?;");
        this.insertPreparedStatement = this.newFormatDB.preparedStatement(
                "INSERT INTO `ShopProperties`(`id`, `propertyName`, "
                + "`filterType`, `valueType`, `oneOfAllValues`) "
                + "VALUES (?,?,?,?,?);");
        this.allKnownNames();
    }

    public void migrationSQL() {
        ResultSet oldFormatRS = this.oldFormatDB.executeQuery("SELECT `id`, `propertyName`, `filterType`, `valueType`, `oneOfAllValues` FROM `ShopProperties`;");
        this.aliasId.clear();
        try {
            while (oldFormatRS.next()) {
                String id = oldFormatRS.getString("id");
                String propertyName = oldFormatRS.getString("propertyName");
                String filterType = MigrationHelper.getFilterType(oldFormatRS.getString("filterType"));
                String valueType = MigrationHelper.getValueType(oldFormatRS.getString("valueType"));
                Integer oneOfAllValues = oldFormatRS.getInt("oneOfAllValues");
                ResultSet amountRS = this.newFormatDB.executeQuery("SELECT count(`id`) as amount FROM `ShopProperties` WHERE `id`='" + id + "';");
                amountRS.first();
                String allNamesKey = generateAllNamesKey(propertyName, filterType, valueType);
                if (this.allNames.get(allNamesKey) != null) {
                    System.out.println("Property [" + id + "] (" + propertyName + ") refers to Property [" + this.allNames.get(allNamesKey) + "]");
                    this.aliasId.put(id, this.allNames.get(allNamesKey));
                } else {
                    this.allNames.put(allNamesKey, id);
                    this.aliasId.put(id, id);
                    if (amountRS.getInt("amount") > 0) {
                        this.sqlUpdate(id, propertyName, filterType, valueType, oneOfAllValues);
                    } else {
                        this.sqlInsert(id, propertyName, filterType, valueType, oneOfAllValues);
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(MigrationShopGroups.class.getName()).log(Level.SEVERE, null, ex);
        }
        MigrationHelper.setPropertiesId(aliasId);
    }

    private void allKnownNames() {
        ResultSet knownRS = this.newFormatDB.executeQuery("SELECT `id`, `propertyName`, `filterType`, `valueType` FROM `ShopProperties`;");
        try {
            while (knownRS.next()) {
                String allNamesKey = generateAllNamesKey(knownRS.getString("propertyName"), knownRS.getString("filterType"), knownRS.getString("valueType"));
                this.allNames.put(allNamesKey, knownRS.getString("id"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(MigrationShopGroups.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String generateAllNamesKey(String propertyName, String filterType, String valueType) {
        return propertyName + "{&}" + filterType + "{&}" + valueType;
    }

    private void sqlUpdate(String id, String propertyName, String filterType, String valueType, Integer oneOfAllValues) {
        System.out.println(Migration.getThisBlock() + " Update Property [" + id + "] - " + propertyName);
        try {
            this.updatePreparedStatement.setString(1, propertyName);
            this.updatePreparedStatement.setString(2, filterType);
            this.updatePreparedStatement.setString(3, valueType);
            this.updatePreparedStatement.setInt(4, oneOfAllValues);
            this.updatePreparedStatement.setString(5, id);
            this.updatePreparedStatement.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(MigrationShopGroups.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sqlInsert(String id, String propertyName, String filterType, String valueType, Integer oneOfAllValues) {
        System.out.println(Migration.getThisBlock() + " Insert Property [" + id + "] - " + propertyName);
        try {
            this.insertPreparedStatement.setString(1, id);
            this.insertPreparedStatement.setString(2, propertyName);
            this.insertPreparedStatement.setString(3, filterType);
            this.insertPreparedStatement.setString(4, valueType);
            this.insertPreparedStatement.setInt(5, oneOfAllValues);
            this.insertPreparedStatement.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(MigrationShopGroups.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
