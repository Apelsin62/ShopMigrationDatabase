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
package ShopMigrationDatabase.General;

import ShopMigrationDatabase.Helpers.XMLHelper;
import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Maxim Zaytsev
 */
public class Configuration {

    private static final String configurePath = "./conf/Configure.xml";
    private static Configuration _instance = null;
    private static Document configureDoc;
    private static final Map<String, Map<String, Map<String, String>>> configuration = new HashMap<String, Map<String, Map<String, String>>>();
    // Properties;
    private static final String[] propertiesMySQLConnections = {"connectionsName", "jdbc", "user", "password"};

    /**
     * Получение конфигурации
     */
    private Configuration() {
        configureDoc = XMLHelper.getXMLDOC(configurePath);
        this.readConfigurationBlockData("MySQLConnections", "MySQLConnections", "Connection", "connectionsName", propertiesMySQLConnections);
    }

    /**
     * Получение блока из файла конфигурации
     *
     * @param BlockName - Название тега блока конфигурации в XML файле
     * @return Node
     */
    private Node getConfigurationBlock(String BlockName) {
        NodeList nodeList = configureDoc.getElementsByTagName(BlockName);
        return nodeList.item(0);
    }

    /**
     * Считываем запрашиваемы данные из файла конфигурации
     *
     * @param configurationName - нвазвание данных конфигурации
     * @param BlockName - Название тега блока конфигурации в XML файле
     * @param ElementName - Название тега элемента блока конфигурации в XML
     * файле
     * @param identificationFieldName - название тега идентифицирующего поля
     * @param properties - список запрашиваемых свойств
     */
    private void readConfigurationBlockData(String configurationName, String BlockName, String ElementName, String identificationFieldName, String[] properties) {
        NodeList elements = XMLHelper.getNodeList(getConfigurationBlock(BlockName), ElementName);
        Map<String, Map<String, String>> configurationData = new HashMap<String, Map<String, String>>();
        for (int s = 0; s < elements.getLength(); s++) {
            Element element = XMLHelper.NodeToElement(elements.item(s));
            String identificationValue = XMLHelper.getElementValue(element, identificationFieldName);
            Map<String, String> configurationDataElement = new HashMap<String, String>();
            for (String propertie : properties) {
                configurationDataElement.put(propertie, XMLHelper.getElementValue(element, propertie));
            }
            configurationData.put(identificationValue, configurationDataElement);
        }
        configuration.put(configurationName, configurationData);
    }

    /**
     * Возвращает значение параметка указаного элемента конфигурации.
     *
     * @param elementName - имя запрошеной конфигурации
     * @param identification - идентификатор элемента конфигурации
     * @param paramName - наименование параметра
     * @return String - значение. В случае ошибки метод вернет null.
     */
    public String getConfigurationElementData(String elementName, String identification, String paramName) {
        Map<String, Map<String, String>> element = configuration.get(elementName);
        if (element != null) {
            Map<String, String> elementData = element.get(identification);
            if (elementData != null) {
                return elementData.get(paramName);
            }
        }
        return null;
    }

    /**
     * Возвращает значение параметка указаного подключения.
     *
     * @param connectionsName имя подключения
     * @param paramName параметр: connectionsName / jdbc / user / password
     * @return String - значение. В случае ошибки метод вернет null.
     */
    public String getMySQLConnectionsData(String connectionsName, String paramName) {
        return this.getConfigurationElementData("MySQLConnections", connectionsName, paramName);
    }

    /**
     * Возвращает значение параметка указаного пути файла.
     *
     * @param pathName имя пути
     * @param paramName параметр: pathName / directory
     * @return String - значение. В случае ошибки метод вернет null.
     */
    public String getFilePathsData(String pathName, String paramName) {
        return this.getConfigurationElementData("FilePaths", pathName, paramName);
    }

    /**
     * Получить singleton объект класса
     *
     * @return Configuration - singleton объект класса
     */
    public synchronized static Configuration getInstance() {
        if (_instance == null) {
            _instance = new Configuration();
        }
        return _instance;
    }
    
}
