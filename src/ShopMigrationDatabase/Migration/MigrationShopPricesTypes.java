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
public class MigrationShopPricesTypes {

    private final MySQLHelper oldFormatDB;
    private final MySQLHelper newFormatDB;
    private final ArrayList<String> sqlList = new ArrayList<>();
    private final ArrayList<String> allTypes = new ArrayList<>();
    private String defaultType = null;

    public MigrationShopPricesTypes(MySQLHelper oldFormatDB, MySQLHelper newFormatDB) {
        this.oldFormatDB = oldFormatDB;
        this.newFormatDB = newFormatDB;
        this.allKnownTypes();
        this.generateMigrationSQL();
    }

    public ArrayList<String> getSqlList() {
        return this.sqlList;
    }

    private void correctDefaultTypes() {
        try {
            ResultSet amountRS = this.newFormatDB.executeQuery("SELECT count(`id`) as amount FROM `ShopPricesTypes` WHERE `default`='1';");
            amountRS.first();
            if (amountRS.getInt("amount") > 0) {
                ResultSet knownRS = this.newFormatDB.executeQuery("SELECT `id` FROM `ShopPricesTypes` WHERE `default`='1';");
                knownRS.next();
                String oldDefaultType = knownRS.getString("id");
                if (this.defaultType == null) {
                    this.defaultType = oldDefaultType;
                } else {
                    this.sqlList.add("UPDATE `ShopPricesTypes` SET `default`='0' WHERE `id`='" + oldDefaultType + "';");
                }
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

    private void generateMigrationSQL() {
        ResultSet oldFormatRS = this.oldFormatDB.executeQuery("SELECT `id`, `typeName`, `default` FROM `ShopPricesTypes`;");
        this.sqlList.clear();
        try {
            while (oldFormatRS.next()) {
                String id = oldFormatRS.getString("id");
                String typeName = oldFormatRS.getString("typeName");
                Integer def = oldFormatRS.getInt("default");
                ResultSet amountRS = this.newFormatDB.executeQuery("SELECT count(`id`) as amount FROM `ShopPricesTypes` WHERE `id`='" + id + "';");
                amountRS.first();
                if (amountRS.getInt("amount") > 0) {
                    this.sqlList.add(this.sqlUpdate(id, typeName, def));
                } else {
                    this.sqlList.add(this.sqlInsert(id, typeName, def));
                }
                if (def > 0) {
                    this.defaultType = id;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(MigrationShopGroups.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.correctDefaultTypes();
        MigrationHelper.setPricesTypes(allTypes, defaultType);
//        for (String sqlList1 : this.allTypes) {
//            System.out.println(sqlList1);
//        }
//        for (String sqlList1 : this.sqlList) {
//            System.out.println(sqlList1);
//        }
//        System.out.println(this.defaultType);
    }

    private String sqlUpdate(String id, String typeName, Integer def) {
        return "UPDATE `ShopPricesTypes` SET `typeName`='" + typeName + "', `default`='" + def + "' WHERE `id`='" + id + "';";
    }

    private String sqlInsert(String id, String typeName, Integer def) {
        this.allTypes.add(id);
        return "INSERT INTO `ShopPricesTypes`(`id`, `typeName`, `typeName_1c`, `default`) VALUES ('" + id + "','" + typeName + "','" + typeName + "','" + def + "');";
    }

}
