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
package ShopMigrationDatabase.Migration.ShopGroups;

import ShopMigrationDatabase.General.SystemConstants;
import ShopMigrationDatabase.Helpers.MySQLHelper;
import ShopMigrationDatabase.Migration.MigrationShopGroups;
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
public class ShopGroupsDataHelper {

    private final MySQLHelper newFormatDB = new MySQLHelper(SystemConstants.getNewFormatDatabaseConnectionsName());
    private final Map<String, ShopGroupElement> groups = new HashMap<>();
    private final Map<String, ArrayList<String>> groupsNodeCildren = new HashMap<>();
    private final Map<String, String> groupsNodeParent = new HashMap<>();
    private final ArrayList<String> rootGroups = new ArrayList<>();

    public ShopGroupsDataHelper() {
        this.getGroupsData();
        this.getGroupsHierarchyData();
        this.getRootGroupsData();
        this.newFormatDB.close();
//        this.testData();
    }

    private void getGroupsData() {
        String query = "SELECT `id`, `groupName`, `shown`, `showInHierarchy`, `systemGroup` FROM `ShopGroups` ORDER BY `groupName` ASC;";
        ResultSet resultSet = this.newFormatDB.executeQuery(query);
        this.groups.clear();
        try {
            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String groupName = resultSet.getString("groupName");
                Integer shown = resultSet.getInt("shown");
                Integer showInHierarchy = resultSet.getInt("showInHierarchy");
                Integer systemGroup = resultSet.getInt("systemGroup");
                this.groups.put(id, new ShopGroupElement(id, groupName, shown, showInHierarchy, systemGroup));
            }
        } catch (SQLException ex) {
            Logger.getLogger(MigrationShopGroups.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void getGroupsHierarchyData() {
        String query = "SELECT SGH.`group`, SGH.`parentGroup` FROM `ShopGroupsHierarchy` as SGH LEFT JOIN `ShopGroups` as SG ON SGH.`group` = SG.`id` ORDER BY SG.`groupName` ASC;";
        ResultSet resultSet = this.newFormatDB.executeQuery(query);
        this.groupsNodeCildren.clear();
        this.groupsNodeParent.clear();
        try {
            while (resultSet.next()) {
                String group = resultSet.getString("group");
                String parentGroup = resultSet.getString("parentGroup");
                ArrayList<String> list = new ArrayList<>();
                if (this.groupsNodeCildren.get(parentGroup) != null) {
                    list.addAll(this.groupsNodeCildren.get(parentGroup));
                }
                list.add(group);
                this.groupsNodeCildren.put(parentGroup, list);
                this.groupsNodeParent.put(group, parentGroup);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MigrationShopGroups.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void getRootGroupsData() {
        String query = "SELECT SG.`id` FROM `ShopGroupsHierarchy` as SGH right join `ShopGroups` as SG on SGH.`group` = SG.`id` where SGH.`group` IS NULL ORDER BY SG.`groupName` ASC;";
        ResultSet resultSet = this.newFormatDB.executeQuery(query);
        this.rootGroups.clear();
        try {
            while (resultSet.next()) {
                String id = resultSet.getString("id");
                this.rootGroups.add(id);
            }
        } catch (SQLException ex) {
            Logger.getLogger(MigrationShopGroups.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Map<String, ShopGroupElement> getGroups() {
        return this.groups;
    }

    public Map<String, ArrayList<String>> getGroupsNodeCildren() {
        return this.groupsNodeCildren;
    }

    public Map<String, String> getGroupsNodeParent() {
        return this.groupsNodeParent;
    }

    public ArrayList<String> getRootGroups() {
        return this.rootGroups;
    }

    public void testData() {
//        System.out.println("Groups");
//        for (Map.Entry<String, ShopGroupElement> entry : this.groups.entrySet()) {
//            ShopGroupElement groupElement = entry.getValue();
//            String id = groupElement.getId();
//            String groupName = groupElement.getGroupName();
//            Integer shown = groupElement.getShown();
//            Integer shownInHierarchy = groupElement.getShowInHierarchy();
//            Integer systemGroup = groupElement.getSystemGroup();
//            System.out.println("Key: " + entry.getKey() + " Value: " + id + " | " + groupName + " | " + shown + " | " + shownInHierarchy + " | " + systemGroup);
//        }
//        System.out.println();
//        System.out.println("GroupsHierarchy");
//        for (Map.Entry<String, ArrayList<String>> entry : this.groupsNodeCildren.entrySet()) {
//            System.out.print("Key: " + entry.getKey() + " Value: ");
//            for (String group : entry.getValue()) {
//                System.out.print(group + " | ");
//            }
//            System.out.println();
//        }
//        System.out.println();
//        System.out.println("RootGroups");
//        for (String rootGroup : rootGroups) {
//            System.out.println(rootGroup);
//        }
        
//        String testId = "c343f723-03a3-11e5-bad4-005056be1f7a";
//        if(groupsHierarchy.get(testId) != null) {
//            for (String test : groupsHierarchy.get(testId)) {
//                System.out.println(test);
//            }
//        } else {
//            System.out.println("нету");
//        }
    }
}
