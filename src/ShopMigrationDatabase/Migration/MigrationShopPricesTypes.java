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
public class MigrationShopPricesTypes {

    private final MySQLHelper oldFormatDB;
    private final MySQLHelper newFormatDB;
    private final ArrayList<String> allTypes = new ArrayList<>();
    private String defaultType = null;
    private final PreparedStatement updatePreparedStatement;
    private final PreparedStatement insertPreparedStatement;
    private final PreparedStatement correctDefaultTypesPreparedStatement;

    public MigrationShopPricesTypes(MySQLHelper oldFormatDB, MySQLHelper newFormatDB) {
        this.oldFormatDB = oldFormatDB;
        this.newFormatDB = newFormatDB;
        this.updatePreparedStatement = this.newFormatDB.preparedStatement(
                "UPDATE `ShopPricesTypes` SET `typeName`=?, `default`=? WHERE `id`=?;");
        this.insertPreparedStatement = this.newFormatDB.preparedStatement(
                "INSERT INTO `ShopPricesTypes`(`id`, `typeName`, `typeName_1c`, "
                + "`default`) VALUES (?,?,?,?);");
        this.correctDefaultTypesPreparedStatement = this.newFormatDB.preparedStatement(
                "UPDATE `ShopPricesTypes` SET `default`='0' WHERE `id`=?;");
        this.allKnownTypes();
    }

    private void getDefaultTypes() {
        try {
            ResultSet oldAmountRS = this.oldFormatDB.executeQuery("SELECT count(`id`) as amount FROM `ShopPricesTypes` WHERE `default`='1';");
            oldAmountRS.first();
            if (oldAmountRS.getInt("amount") > 0) {
                ResultSet oldKnownRS = this.oldFormatDB.executeQuery("SELECT `id` FROM `ShopPricesTypes` WHERE `default`='1';");
                oldKnownRS.next();
                this.defaultType = oldKnownRS.getString("id");
                this.correctDefaultTypes();
            } else {
                ResultSet newAmountRS = this.newFormatDB.executeQuery("SELECT count(`id`) as amount FROM `ShopPricesTypes` WHERE `default`='1';");
                newAmountRS.first();
                if (newAmountRS.getInt("amount") > 0) {
                    ResultSet newKnownRS = this.newFormatDB.executeQuery("SELECT `id` FROM `ShopPricesTypes` WHERE `default`='1';");
                    newKnownRS.next();
                    this.defaultType = newKnownRS.getString("id");
                } else {
                    this.defaultType = null;
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(MigrationShopGroups.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void correctDefaultTypes() {
        try {
            ResultSet newAmountRS = this.newFormatDB.executeQuery("SELECT count(`id`) as amount FROM `ShopPricesTypes` WHERE `default`='1';");
            newAmountRS.first();
            if (newAmountRS.getInt("amount") > 0) {
                ResultSet knownRS = this.newFormatDB.executeQuery("SELECT `id` FROM `ShopPricesTypes` WHERE `default`='1';");
                knownRS.next();
                String oldDefaultType = knownRS.getString("id");
                System.out.println(Migration.getThisBlock() + " Unset default prices type for [" + oldDefaultType + "]");
                this.correctDefaultTypesPreparedStatement.setString(1, oldDefaultType);
                this.correctDefaultTypesPreparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            Logger.getLogger(MigrationShopGroups.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void allKnownTypes() {
        ResultSet knownNamesRS = this.newFormatDB.executeQuery("SELECT `id` FROM `ShopPricesTypes`;");
        try {
            while (knownNamesRS.next()) {
                this.allTypes.add(knownNamesRS.getString("id"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(MigrationShopGroups.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void migrationSQL() {
        ResultSet oldFormatRS = this.oldFormatDB.executeQuery("SELECT `id`, `typeName`, `default` FROM `ShopPricesTypes`;");
        try {
            this.getDefaultTypes();
            while (oldFormatRS.next()) {
                String id = oldFormatRS.getString("id");
                String typeName = oldFormatRS.getString("typeName");
                Integer def = oldFormatRS.getInt("default");
                ResultSet amountRS = this.newFormatDB.executeQuery("SELECT count(`id`) as amount FROM `ShopPricesTypes` WHERE `id`='" + id + "';");
                amountRS.first();
                if (amountRS.getInt("amount") > 0) {
                    this.sqlUpdate(id, typeName, def);
                } else {
                    this.sqlInsert(id, typeName, def);
                }
                if (this.defaultType == null) {
                    this.defaultType = id;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(MigrationShopGroups.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sqlUpdate(String id, String typeName, Integer def) {
        System.out.println(Migration.getThisBlock() + " Update Prices Type [" + id + "] - " + typeName);
        try {
            this.updatePreparedStatement.setString(1, typeName);
            this.updatePreparedStatement.setInt(2, def);
            this.updatePreparedStatement.setString(3, id);
            this.updatePreparedStatement.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(MigrationShopPricesTypes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sqlInsert(String id, String typeName, Integer def) {
        System.out.println(Migration.getThisBlock() + " Insert Prices Type [" + id + "] - " + typeName);
        try {
            this.allTypes.add(id);
            this.insertPreparedStatement.setString(1, id);
            this.insertPreparedStatement.setString(2, typeName);
            this.insertPreparedStatement.setString(3, typeName);
            this.insertPreparedStatement.setInt(4, def);
            this.insertPreparedStatement.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(MigrationShopPricesTypes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
