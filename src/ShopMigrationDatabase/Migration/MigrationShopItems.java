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
public class MigrationShopItems {

    private final MySQLHelper oldFormatDB;
    private final MySQLHelper newFormatDB;
    private final ArrayList<String> sqlList = new ArrayList<>();
    private final ArrayList<String> allItems = new ArrayList<>();

    public MigrationShopItems(MySQLHelper oldFormatDB, MySQLHelper newFormatDB) {
        this.oldFormatDB = oldFormatDB;
        this.newFormatDB = newFormatDB;
        this.allKnownItems();
        this.generateMigrationSQL();
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

    public ArrayList<String> getSqlList() {
        return this.sqlList;
    }

    private void generateMigrationSQL() {
        ResultSet oldFormatRS = this.oldFormatDB.executeQuery("SELECT `id`, `itemName`, `group`, `action`, `amount`, `minAmount`, `description`, `shown` FROM `ShopItems`;");
        this.sqlList.clear();
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
                    this.sqlList.add(this.sqlUpdate(id, itemName, group, action, amount, minAmount, description, shown));
                } else {
                    this.sqlList.add(this.sqlInsert(id, itemName, group, action, amount, minAmount, description, shown));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(MigrationShopGroups.class.getName()).log(Level.SEVERE, null, ex);
        }
        MigrationHelper.setItemsId(allItems);
//        for (String sqlList1 : this.allItems) {
//            System.out.println(sqlList1);
//        }
//        for (String sqlList1 : this.sqlList) {
//            System.out.println(sqlList1);
//        }
    }

    private String sqlUpdate(String id, String itemName, String group, Integer action, Float amount, Float minAmount, String description, Integer shown) {
        return "UPDATE `ShopItems` SET "
                + "`itemName`='" + itemName + "',"
                + "`group`='" + group + "',"
                + "`action`='" + action + "',"
                + "`totalAmount`='" + String.format("%f",amount).replace(',','.') + "',"
                + "`minAmount`='" + String.format("%f",minAmount).replace(',','.') + "',"
                + "`description`='" + description + "',"
                + "`shown`='" + shown + "' WHERE `id`='" + id + "';";
    }

    private String sqlInsert(String id, String itemName, String group, Integer action, Float amount, Float minAmount, String description, Integer shown) {
        this.allItems.add(id);
        return "INSERT INTO `ShopItems`(`id`, `itemName`, `article`, "
                + "`directory`, `directoryPath`, `status`, `type`, "
                + "`pricePer`, `serviceCenter`, `action`, `shown`, "
                + "`toRemove`, `totalAmount`, `minAmount`, "
                + "`description`, `group`) VALUES "
                + "('"+id+"','"+itemName+"','article','directory',"
                + "'directoryPath','status','type','pricePer',"
                + "'serviceCenter','"+action+"','"+shown+"','0',"
                + "'"+String.format("%f",amount).replace(',','.')+"','"+String.format("%f",minAmount).replace(',','.')+"','"+description+"',"
                + "'"+group+"');";
    }
}
