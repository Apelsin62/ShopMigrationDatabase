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
public class MigrationShopItemsPrices {

    private final MySQLHelper oldFormatDB;
    private final MySQLHelper newFormatDB;
    private final PreparedStatement updatePreparedStatement;
    private final PreparedStatement insertPreparedStatement;

    private final ArrayList<String> allItems = new ArrayList<>();
    private final ArrayList<String> allPricesTypes = new ArrayList<>();
    private String defaultPriceType;

    public MigrationShopItemsPrices(MySQLHelper oldFormatDB, MySQLHelper newFormatDB) {
        this.oldFormatDB = oldFormatDB;
        this.newFormatDB = newFormatDB;
        this.updatePreparedStatement = this.newFormatDB.preparedStatement(
                "UPDATE `ShopItemsPrices` SET `value`=? WHERE `item`=? AND `price`=?;");
        this.insertPreparedStatement = this.newFormatDB.preparedStatement(
                "INSERT INTO `ShopItemsPrices`(`item`, `price`, `value`) VALUES (?,?,?);");
        this.getNeedData();
    }

    public void migrationSQL() {
        for (String item : allItems) {
            this.generateMigrationSQLForItem(item);
        }
    }

    private void getNeedData() {
        this.getAllItems();
        this.getAllPricesTypes();
        this.getDefaultPricesTypes();
    }

    private void getAllItems() {
        this.allItems.clear();
        try {
            ResultSet knownRS = this.newFormatDB.executeQuery("SELECT `id` FROM `ShopItems`;");
            while (knownRS.next()) {
                this.allItems.add(knownRS.getString("id"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(MigrationShopItemsPrices.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void getAllPricesTypes() {
        this.allPricesTypes.clear();
        try {
            ResultSet knownRS = this.newFormatDB.executeQuery("SELECT `id` FROM `ShopPricesTypes`;");
            while (knownRS.next()) {
                this.allPricesTypes.add(knownRS.getString("id"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(MigrationShopItemsPrices.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void getDefaultPricesTypes() {
        try {
            ResultSet knownRS = this.newFormatDB.executeQuery("SELECT `id` FROM `ShopPricesTypes` WHERE `default` = '1';");
            knownRS.first();
            this.defaultPriceType = knownRS.getString("id");
        } catch (SQLException ex) {
            Logger.getLogger(MigrationShopItemsPrices.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private Float getDefaultPricesValue(String item) {
        Float defaultValue = 0F;
        try {
            ResultSet oldAmountRS = this.oldFormatDB.executeQuery("SELECT count(`item`) as amount FROM `ShopItemsPrices` WHERE `item`='" + item + "' AND `price`='" + this.defaultPriceType + "';");
            oldAmountRS.first();
            if (oldAmountRS.getInt("amount") > 0) {
                ResultSet oldValueRS = this.oldFormatDB.executeQuery("SELECT `value` FROM `ShopItemsPrices` WHERE `item`='" + item + "' AND `price`='" + this.defaultPriceType + "';");
                oldValueRS.first();
                defaultValue = oldValueRS.getFloat("value");
            } else {
                ResultSet newAmountRS = this.newFormatDB.executeQuery("SELECT count(`item`) as amount FROM `ShopItemsPrices` WHERE `item`='" + item + "' AND `price`='" + this.defaultPriceType + "';");
                newAmountRS.first();
                if (newAmountRS.getInt("amount") > 0) {
                    ResultSet newValueRS = this.newFormatDB.executeQuery("SELECT `value` FROM `ShopItemsPrices` WHERE `item`='" + item + "' AND `price`='" + this.defaultPriceType + "';");
                    newValueRS.first();
                    defaultValue = newValueRS.getFloat("value");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(MigrationShopItemsPrices.class.getName()).log(Level.SEVERE, null, ex);
        }
        return defaultValue;
    }

    private void generateMigrationSQLForItem(String item) {
        System.out.println(Migration.getThisBlock() + " Set Prices for Item [" + item + "]:");
        for (String priceType : allPricesTypes) {
            try {
                ResultSet oldAmountRS = this.oldFormatDB.executeQuery("SELECT count(`item`) as amount FROM `ShopItemsPrices` WHERE `item`='" + item + "' AND `price`='" + priceType + "';");
                oldAmountRS.first();
                Boolean inOldDB = oldAmountRS.getInt("amount") > 0;
                ResultSet newAmountRS = this.newFormatDB.executeQuery("SELECT count(`item`) as amount FROM `ShopItemsPrices` WHERE `item`='" + item + "' AND `price`='" + priceType + "';");
                newAmountRS.first();
                Boolean inNewDB = newAmountRS.getInt("amount") > 0;
                if (inOldDB && inNewDB) {
                    // обновить
                    ResultSet oldRS = this.oldFormatDB.executeQuery("SELECT `value` FROM `ShopItemsPrices` WHERE `item`='" + item + "' AND `price`='" + priceType + "';");
                    oldRS.first();
                    this.sqlUpdate(item, priceType, oldRS.getFloat("value"));
                } else if (inOldDB && !inNewDB) {
                    // Добавить из старой базы
                    ResultSet oldRS = this.oldFormatDB.executeQuery("SELECT `value` FROM `ShopItemsPrices` WHERE `item`='" + item + "' AND `price`='" + priceType + "';");
                    oldRS.first();
                    this.sqlInsert(item, priceType, oldRS.getFloat("value"));
                } else if (!inOldDB && !inNewDB) {
                    // Добавить значене по умолчанию
                    Float defaultValue = this.getDefaultPricesValue(item);
                    this.sqlInsert(item, priceType, defaultValue);
                }
            } catch (SQLException ex) {
                Logger.getLogger(MigrationShopItemsPrices.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void sqlUpdate(String item, String price, Float value) {
        System.out.println(" - Update Price for Price Type [" + price + "] and set value - " + String.format("%f", value));
        try {
            this.updatePreparedStatement.setFloat(1, value);
            this.updatePreparedStatement.setString(2, item);
            this.updatePreparedStatement.setString(3, price);
            this.updatePreparedStatement.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(MigrationShopGroups.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sqlInsert(String item, String price, Float value) {
        System.out.println(" - Insert Price for Price Type [" + price + "] and set value - " + String.format("%f", value));
        try {
            this.insertPreparedStatement.setString(1, item);
            this.insertPreparedStatement.setString(2, price);
            this.insertPreparedStatement.setFloat(3, value);
            this.insertPreparedStatement.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(MigrationShopGroups.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
