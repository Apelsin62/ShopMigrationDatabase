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
    private final ArrayList<String> sqlList = new ArrayList<>();

    private ArrayList<String> allItems = new ArrayList<>();
    private ArrayList<String> allPricesTypes = new ArrayList<>();
    private String defaultPriceType = null;

    public MigrationShopItemsPrices(MySQLHelper oldFormatDB, MySQLHelper newFormatDB) {
        this.oldFormatDB = oldFormatDB;
        this.newFormatDB = newFormatDB;
        this.getNeedData();
        this.generateMigrationSQL();
    }

    public ArrayList<String> getSqlList() {
        return this.sqlList;
    }

    private void getNeedData() {
        this.allItems = MigrationHelper.getItemsId();
        this.allPricesTypes = MigrationHelper.getPricesTypes();
        this.defaultPriceType = MigrationHelper.getDefaultPricesTypes();
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
        Float defaultValue = this.getDefaultPricesValue(item);
        for (String priceType : allPricesTypes) {
//            System.out.println(priceType);
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
                    this.sqlList.add(this.sqlUpdate(item, priceType, oldRS.getFloat("value")));
                } else if (inOldDB && !inNewDB) {
                    // Добавить из старой базы
                    ResultSet oldRS = this.oldFormatDB.executeQuery("SELECT `value` FROM `ShopItemsPrices` WHERE `item`='" + item + "' AND `price`='" + priceType + "';");
                    oldRS.first();
                    this.sqlList.add(this.sqlInsert(item, priceType, oldRS.getFloat("value")));
                } else if (!inOldDB && !inNewDB) {
                    // Добавить значене по умолчанию
                    this.sqlList.add(this.sqlInsert(item, priceType, defaultValue));
                }
            } catch (SQLException ex) {
                Logger.getLogger(MigrationShopItemsPrices.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void generateMigrationSQL() {
        for (String item : allItems) {
            this.generateMigrationSQLForItem(item);
        }
//        for (String sqlList1 : this.sqlList) {
//            System.out.println(sqlList1);
//        }
    }

    private String sqlUpdate(String item, String price, Float value) {
        return "UPDATE `ShopItemsPrices` SET `value`='" + String.format("%f",value).replace(',','.') + "' WHERE `item`='" + item + "' AND `price`='" + price + "';";
    }

    private String sqlInsert(String item, String price, Float value) {
        return "INSERT INTO `ShopItemsPrices`(`item`, `price`, `value`) VALUES ('" + item + "','" + price + "','" + String.format("%f",value).replace(',','.') + "');";
    }
}
