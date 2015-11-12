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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Maxim Zaytsev
 */
public class MigrationShopPropertiesMeasure {

    private final MySQLHelper oldFormatDB;
    private final MySQLHelper newFormatDB;
    private final ArrayList<String> allMeasures = new ArrayList<>();
    private Map<String, String> measuresFullName = new HashMap<>();
    private final ArrayList<String> sqlList = new ArrayList<>();

    public MigrationShopPropertiesMeasure(MySQLHelper oldFormatDB, MySQLHelper newFormatDB) {
        this.oldFormatDB = oldFormatDB;
        this.newFormatDB = newFormatDB;
        this.allKnownMeasures();
        this.generateMigrationSQL();
    }

    private void allKnownMeasures() {
        ResultSet knownRS = this.newFormatDB.executeQuery("SELECT `measureS`, `measureF` FROM `ShopMeasure` GROUP BY `measureS`;");
        try {
            while (knownRS.next()) {
                this.allMeasures.add(knownRS.getString("measureS"));
                this.measuresFullName.put(knownRS.getString("measureS"), knownRS.getString("measureF"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(MigrationShopGroups.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ArrayList<String> getSqlList() {
        return this.sqlList;
    }

    private void generateMigrationSQL() {
        this.sqlList.clear();
        for (String measure : allMeasures) {
            ResultSet oldFormatRS = this.oldFormatDB.executeQuery("SELECT `property` FROM `ShopItemsPropertiesValues` WHERE `measure`='" + measure + "' GROUP BY `property`;");
            try {
                while (oldFormatRS.next()) {
                    String property = oldFormatRS.getString("property");
                    ResultSet amountRS = this.newFormatDB.executeQuery("SELECT count(`property`) as amount FROM `ShopPropertiesMeasure` WHERE `property`='" + property + "';");
                    amountRS.first();
                    if (amountRS.getInt("amount") > 0) {
                        this.sqlList.add(this.sqlUpdate(property, measure));
                    } else {
                        this.sqlList.add(this.sqlInsert(property, measure));
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(MigrationShopGroups.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
//        for (String sqlList1 : this.sqlList) {
//            System.out.println(sqlList1);
//        }
    }

    private String sqlUpdate(String property, String measure) {
        return "UPDATE `ShopPropertiesMeasure` SET `property`='" + property + "',`measure`='" + this.measuresFullName.get(measure) + "';";
    }

    private String sqlInsert(String property, String measure) {
        return "INSERT INTO `ShopPropertiesMeasure`(`property`, `measure`) VALUES ('" + property + "','" + this.measuresFullName.get(measure) + "');";
    }
}
