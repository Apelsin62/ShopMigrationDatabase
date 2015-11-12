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
import ShopMigrationDatabase.Migration.ShopGroups.ShopGroupsHelper;
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
public class MigrationShopPropertiesInGroups {

    private final MySQLHelper oldFormatDB;
    private final MySQLHelper newFormatDB;
    private final ShopGroupsHelper shopGroupsHelper = new ShopGroupsHelper();
    private final ArrayList<String> sqlList = new ArrayList<>();
    private Map<String, ArrayList<String>> groupsChildren = new HashMap<>();
    private Map<String, ArrayList<String>> groupsPath = new HashMap<>();
    private Map<String, Integer> propertySequence = new HashMap<>();



    public MigrationShopPropertiesInGroups(MySQLHelper oldFormatDB, MySQLHelper newFormatDB) {
        this.oldFormatDB = oldFormatDB;
        this.newFormatDB = newFormatDB;
        this.groupsChildren = this.shopGroupsHelper.getGroupsChildren();
        this.groupsPath = this.shopGroupsHelper.getGroupsPath();
        this.generateMigrationSQL();
    }

    public ArrayList<String> getSqlList() {
        return this.sqlList;
    }

    private void generateMigrationSQL() {
        ResultSet oldFormatRS = this.oldFormatDB.executeQuery("SELECT `group`, `property`, `sequence` FROM `ShopPropertiesInGroups` ORDER BY `sequence` ASC;");
        this.sqlList.clear();
        try {
            while (oldFormatRS.next()) {
                String group = oldFormatRS.getString("group");
                String property = oldFormatRS.getString("property");
                String id = group.concat(property);
                Integer sequence = oldFormatRS.getInt("sequence");
                ResultSet amountRS = this.newFormatDB.executeQuery("SELECT count(`id`) as amount FROM `ShopPropertiesInGroups` WHERE `group`='" + group + "' AND `property`='" + property + "';");
                amountRS.first();
                if (amountRS.getInt("amount") > 0) {
                    this.sqlList.addAll(this.sqlUpdate(id, group, property, sequence));
                } else {
                    this.sqlList.addAll(this.sqlInsert(id, group, property, sequence));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(MigrationShopGroups.class.getName()).log(Level.SEVERE, null, ex);
        }
//        for (String sqlList1 : this.sqlList) {
//            System.out.println(sqlList1);
//        }
    }

    private ArrayList<String> sqlUpdate(String id, String group, String property, Integer sequence) {
        ArrayList<String> sqlList = new ArrayList<>();
        sqlList.add("UPDATE `ShopPropertiesInGroups` SET `id`='" + id + "',`sequence`='" + sequence + "' WHERE `group`='" + group + "' AND `property`='" + property + "';");
        return sqlList;
    }

    private ArrayList<String> sqlInsert(String id, String group, String property, Integer sequence) {
        ArrayList<String> sqlList = new ArrayList<>();
        if(this.checkParentGroup(group, property)) {
            sqlList.add("INSERT INTO `ShopPropertiesInGroups`(`id`, `group`, `property`, `sequence`) VALUES ('" + id + "','" + group + "','" + property + "','" + sequence + "');");
            sqlList.add("INSERT INTO `ShopPropertiesInGroupsRanking`(`group`, `propertyInGroup`, `sequence`, `shown`) VALUES ('" + group + "','" + id + "','" + this.getPropertiesInGroupsRankingSequence(group) + "','1');");
            ArrayList<String> children = this.groupsChildren.get(group);
            for (String child : children) {
                sqlList.add("INSERT INTO `ShopPropertiesInGroupsRanking`(`group`, `propertyInGroup`, `sequence`, `shown`) VALUES ('" + child + "','" + id + "','" + this.getPropertiesInGroupsRankingSequence(child) + "','1');");
            }
        } else {
            System.out.println("Свойство " + property + " уже используется в одном из родителей группы " + group);
        }
        return sqlList;
    }
    
    private Integer getPropertiesInGroupsRankingSequence(String group) {
        if(this.propertySequence.get(group) != null) {
            this.propertySequence.put(group, this.propertySequence.get(group) + 1);
        } else {
            this.propertySequence.put(group, this.getPropertiesInGroupsRankingStartSequence(group));
        }
        return this.propertySequence.get(group);
    }
    
    private Integer getPropertiesInGroupsRankingStartSequence(String group) {
        try {
            ResultSet amountRS = this.newFormatDB.executeQuery("SELECT count(`propertyInGroup`) as amount FROM `ShopPropertiesInGroupsRanking` WHERE `group`='" + group + "';");
            amountRS.first();
            return amountRS.getInt("amount") + 1;
        } catch (SQLException ex) {
            Logger.getLogger(MigrationShopPropertiesInGroups.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 1;
    }
    
    private Boolean checkParentGroup(String group, String property) {
        ArrayList<String> groupPath = this.groupsPath.get(group);
        Boolean successfully = true;
        for (String parentGroup : groupPath) {
            try {
                ResultSet amountRS = this.newFormatDB.executeQuery("SELECT count(`id`) as amount FROM `ShopPropertiesInGroups` WHERE `group`='" + parentGroup + "' AND `property`='" + property + "';");
                amountRS.first();
                if (amountRS.getInt("amount") > 0) {
                    successfully = false;
                }
            } catch (SQLException ex) {
                Logger.getLogger(MigrationShopPropertiesInGroups.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return successfully;
    }
}
