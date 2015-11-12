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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Maxim Zaytsev
 */
public class ShopGroupsHelper {

    private final ShopGroupsDataHelper shopGroupsDataHelper = new ShopGroupsDataHelper();
    private Map<String, ShopGroupElement> groups = new HashMap<>();
    private Map<String, ArrayList<String>> groupsNodeCildren = new HashMap<>();
    private Map<String, String> groupsNodeParent = new HashMap<>();
    private ArrayList<String> rootGroups = new ArrayList<>();
    private final Map<String, ArrayList<String>> groupsChildren = new HashMap<>();
    private final Map<String, ArrayList<String>> groupsPath = new HashMap<>();

    public ShopGroupsHelper() {
        this.groups = this.shopGroupsDataHelper.getGroups();
        this.groupsNodeCildren = this.shopGroupsDataHelper.getGroupsNodeCildren();
        this.groupsNodeParent = this.shopGroupsDataHelper.getGroupsNodeParent();
        this.rootGroups = this.shopGroupsDataHelper.getRootGroups();
        this.getAllGroupsChildren();
        this.getFullGroupsPath();
    }
    
    private void getAllGroupsChildren() {
        for (Map.Entry<String, ShopGroupElement> entry : this.groups.entrySet()) {
            ShopGroupElement groupElement = entry.getValue();
            String id = groupElement.getId();
            this.groupsChildren.put(id, getGroupChildren(id, new ArrayList<>()));
        }
//        for (String child : groupsChildren.get("6f39f780-260e-11e5-80c1-00155d3703e2")) {
//            System.out.println(child);
//        }
    }
    
    private ArrayList<String> getGroupChildren(String thisId, ArrayList<String> children) {
        if(this.groupsNodeCildren.get(thisId) != null) {
            for (String child : this.groupsNodeCildren.get(thisId)) {
                ArrayList<String> childChildrens = new ArrayList<String>();
                childChildrens.addAll(getGroupChildren(child, children));
                children.clear();
                children.addAll(childChildrens);
                children.add(child);
            }
        }
        return children;
    }
    
    private void getFullGroupsPath() {
        for (Map.Entry<String, ShopGroupElement> entry : this.groups.entrySet()) {
            ShopGroupElement groupElement = entry.getValue();
            String id = groupElement.getId();
            this.groupsPath.put(id, getGroupsPath(id, new ArrayList<>()));
        }
//        for (String path : groupsPath.get("01d29291-26d1-11e5-80c1-00155d3703e2")) {
//            System.out.println(path);
//        }
    }
    
    private ArrayList<String> getGroupsPath(String thisId, ArrayList<String> path) {
        if(this.groupsNodeParent.get(thisId) != null) {
            ArrayList<String> prePath = new ArrayList<String>();
            prePath.addAll(getGroupsPath(this.groupsNodeParent.get(thisId), path));
            path.clear();
            path.addAll(prePath);
            path.add(this.groupsNodeParent.get(thisId));
        }
        return path;
    }

    public Map<String, ShopGroupElement> getGroups() {
        return groups;
    }

    public ArrayList<String> getRootGroups() {
        return rootGroups;
    }

    public Map<String, ArrayList<String>> getGroupsChildren() {
        return groupsChildren;
    }

    public Map<String, ArrayList<String>> getGroupsPath() {
        return groupsPath;
    }
}
