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

import ShopMigrationDatabase.Helpers.MySQLHelper;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Maxim Zaytsev
 */
public class MigrationShopItems {

    private final MySQLHelper oldFormatDB;
    private final MySQLHelper newFormatDB;
    private final ArrayList<String> allItems = new ArrayList<>();
    private final PreparedStatement updatePreparedStatement;
    private final PreparedStatement insertPreparedStatement;

    public MigrationShopItems(MySQLHelper oldFormatDB, MySQLHelper newFormatDB) {
        this.oldFormatDB = oldFormatDB;
        this.newFormatDB = newFormatDB;
        this.updatePreparedStatement = this.newFormatDB.preparedStatement(
                "UPDATE `ShopItems` SET `itemName`=?,`group`=?,`action`=?,"
                + "`totalAmount`=?,`minAmount`=?,`description`=?,"
                + "`shown`=? WHERE `id`=?;");
        this.insertPreparedStatement = this.newFormatDB.preparedStatement(
                "INSERT INTO `ShopItems`(`id`, `itemName`, `article`, "
                + "`directory`, `directoryPath`, `status`, `type`, "
                + "`pricePer`, `serviceCenter`, `action`, `shown`, "
                + "`toRemove`, `totalAmount`, `minAmount`, "
                + "`description`, `group`) VALUES "
                + "(?,?,'article','directory',"
                + "'directoryPath','status','type','pricePer',"
                + "'serviceCenter',?,?,'0',?,?,?,?);");
        this.allKnownItems();
    }

    private void allKnownItems() {
        ResultSet knownRS = this.newFormatDB.executeQuery("SELECT `id` FROM `ShopItems`;");
        try {
            while (knownRS.next()) {
                this.allItems.add(knownRS.getString("id"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(MigrationShopGroups.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void migrationSQL() {
        ResultSet oldFormatRS = this.oldFormatDB.executeQuery("SELECT `id`, `itemName`, `group`, `action`, `amount`, `minAmount`, `description`, `shown` FROM `ShopItems`;");
        try {
            while (oldFormatRS.next()) {
                String id = oldFormatRS.getString("id");
                String itemName = oldFormatRS.getString("itemName");
                String group = oldFormatRS.getString("group");
                Integer action = oldFormatRS.getInt("action");
                Float amount = oldFormatRS.getFloat("amount");
                Float minAmount = oldFormatRS.getFloat("minAmount");
                String description = oldFormatRS.getString("description");
                Integer shown = oldFormatRS.getInt("shown");
                ResultSet amountRS = this.newFormatDB.executeQuery("SELECT count(`id`) as amount FROM `ShopItems` WHERE `id`='" + id + "';");
                amountRS.first();
                if (amountRS.getInt("amount") > 0) {
                    this.sqlUpdate(id, itemName, group, action, amount, minAmount, description, shown);
                } else {
                    this.sqlInsert(id, itemName, group, action, amount, minAmount, description, shown);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(MigrationShopGroups.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sqlUpdate(String id, String itemName, String group, Integer action, Float amount, Float minAmount, String description, Integer shown) {
        System.out.println(Migration.getThisBlock() + " Update Item [" + id + "] - " + itemName);
        try {
            updatePreparedStatement.setString(1, itemName);
            updatePreparedStatement.setString(2, group);
            updatePreparedStatement.setInt(3, action);
            updatePreparedStatement.setFloat(4, amount);
            updatePreparedStatement.setFloat(5, minAmount);
            updatePreparedStatement.setString(6, description);
            updatePreparedStatement.setInt(7, shown);
            updatePreparedStatement.setString(8, id);
            updatePreparedStatement.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(MigrationShopItems.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sqlInsert(String id, String itemName, String group, Integer action, Float amount, Float minAmount, String description, Integer shown) {
        System.out.println(Migration.getThisBlock() + " Insert Item [" + id + "] - " + itemName);
        try {
            this.allItems.add(id);
            insertPreparedStatement.setString(1, id);
            insertPreparedStatement.setString(2, itemName);
            insertPreparedStatement.setInt(3, action);
            insertPreparedStatement.setInt(4, shown);
            insertPreparedStatement.setFloat(5, amount);
            insertPreparedStatement.setFloat(6, minAmount);
            insertPreparedStatement.setString(7, description);
            insertPreparedStatement.setString(8, group);
            insertPreparedStatement.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(MigrationShopItems.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
