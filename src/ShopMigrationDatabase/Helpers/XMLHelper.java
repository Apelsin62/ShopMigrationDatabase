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
package ShopMigrationDatabase.Helpers;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author Maxim Zaytsev
 */
public class XMLHelper {

    /**
     * Получить XML файл в виде Document.
     *
     * @param filePath - путь к файлу
     * @return Document - преобразованный в Document XML файл. В случае ошибки
     * метод вернет null.
     */
    public static Document getXMLDOC(String filePath) {
        DocumentBuilderFactory dbf;
        DocumentBuilder db;
        Document doc;
        File file;
        try {
            dbf = DocumentBuilderFactory.newInstance();
            db = dbf.newDocumentBuilder();
            file = new File(filePath);
            doc = db.parse(file);
            doc.getDocumentElement().normalize();
            return doc;
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(XMLHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Преобразовать узел документа в элемент
     *
     * @param node - узел
     * @return Element. - элемент В случае ошибки метод вернет null.
     */
    public static Element NodeToElement(Node node) {
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            return (Element) node;
        }
        return null;
    }

    /**
     * Получение списка узлов из текущего узла по имени тега.
     *
     * @param node - узел
     * @param tagName - тег
     * @return NodeList - список узлов
     */
    public static NodeList getNodeList(Node node, String tagName) {
        return getNodeList(NodeToElement(node), tagName);
    }

    /**
     * Получение списка узлов из текущего элемента по имени тега.
     *
     * @param element - элемент
     * @param tagName - тег
     * @return NodeList - список узлов
     */
    public static NodeList getNodeList(Element element, String tagName) {
        return element.getElementsByTagName(tagName);
    }

    /**
     * Получение первого узла из текущего списка узлов.
     *
     * @param nodeList - список узлов
     * @return Node
     */
    public static Node getNode(NodeList nodeList) {
        return nodeList.item(0);
    }

    /**
     * Получение n-го узла из текущего списка узлов.
     *
     * @param nodeList - список узлов
     * @param n - номер узла
     * @return Node. В случае ошибки метод вернет null.
     */
    public static Node getNode(NodeList nodeList, Integer n) {
        return nodeList.item(n);
    }

    /**
     * Получение первого дочернего узла из списка узлов, найденого по имени тега
     * в текущем узле.
     *
     * @param node - текущий узел
     * @param tagName - имя тега
     * @return Node. В случае ошибки метод вернет null.
     */
    public static Node getNode(Node node, String tagName) {
        NodeList nodeList = XMLHelper.getNodeList(node, tagName);
        return nodeList.item(0);
    }

    /**
     * Получение первого дочернего узла из списка узлов, найденого по имени тега
     * в текущем узле.
     *
     * @param node - текущий узел
     * @param tagName - имя тега
     * @param n - номер узла
     * @return Node. В случае ошибки метод вернет null.
     */
    public static Node getNode(Node node, String tagName, Integer n) {
        NodeList nodeList = XMLHelper.getNodeList(node, tagName);
        return nodeList.item(n);
    }

    /**
     * Получение первого дочернего узла из списка узлов, найденого по имени тега
     * в текущем елементе.
     *
     * @param element - текущий элемент
     * @param tagName - имя тега
     * @return Node. В случае ошибки метод вернет null.
     */
    public static Node getNode(Element element, String tagName) {
        NodeList nodeList = XMLHelper.getNodeList(element, tagName);
        return nodeList.item(0);
    }

    /**
     * Получение первого дочернего узла из списка узлов, найденого по имени тега
     * в текущем елементе.
     *
     * @param element - текущий элемент
     * @param tagName - имя тега
     * @param n - номер узла
     * @return Node. В случае ошибки метод вернет null.
     */
    public static Node getNode(Element element, String tagName, Integer n) {
        NodeList nodeList = XMLHelper.getNodeList(element, tagName);
        return nodeList.item(n);
    }

    /**
     * Полученрие узла из набора узлов собранных по именам тегов из текущего
     * узла.
     *
     * @param node - текущий узел
     * @param tagNames - список имет тегов
     * @return Node. В случае ошибки метод вернет null.
     */
    public static Node getNode(Node node, String[] tagNames) {
        Node thisNode = node;
        for (String tagName : tagNames) {
            NodeList nodeList = XMLHelper.getNodeList(thisNode, tagName);
            thisNode = nodeList.item(0);
        }
        return thisNode;
    }

    /**
     * Полученрие узла из набора узлов собранных по именам тегов из текущего
     * узла.
     *
     * @param element - текущий элемент
     * @param tagNames - список имет тегов
     * @return Node. В случае ошибки метод вернет null.
     */
    public static Node getNode(Element element, String[] tagNames) {
        Node thisNode = element;
        for (String tagName : tagNames) {
            NodeList nodeList = XMLHelper.getNodeList(thisNode, tagName);
            thisNode = nodeList.item(0);
        }
        return thisNode;
    }

    /**
     * Получение элемента из первого узла из текущего списка узлов.
     *
     * @param nodeList - список узлов
     * @return Element
     */
    public static Element getElement(NodeList nodeList) {
        return NodeToElement(getNode(nodeList));
    }

    /**
     * Получение элемента из n-го узла из текущего списка узлов.
     *
     * @param nodeList - список узлов
     * @param n - номер узла
     * @return Element. В случае ошибки метод вернет null.
     */
    public static Element getElement(NodeList nodeList, Integer n) {
        return NodeToElement(getNode(nodeList, n));
    }

    /**
     * Получение элемента из первого дочернего узла из списка узлов, найденого
     * по имени тега в текущем узле.
     *
     * @param node - текущий узел
     * @param tagName - имя тега
     * @return Element. В случае ошибки метод вернет null.
     */
    public static Element getElement(Node node, String tagName) {
        return NodeToElement(getNode(node, tagName));
    }

    /**
     * Получение элемента из первого дочернего узла из списка узлов, найденого
     * по имени тега в текущем узле.
     *
     * @param node - текущий узел
     * @param tagName - имя тега
     * @param n - номер узла
     * @return Element. В случае ошибки метод вернет null.
     */
    public static Element getElement(Node node, String tagName, Integer n) {
        return NodeToElement(getNode(node, tagName, n));
    }

    /**
     * Получение элемента из первого дочернего узла из списка узлов, найденого
     * по имени тега в текущем елементе.
     *
     * @param element - текущий элемент
     * @param tagName - имя тега
     * @return Element. В случае ошибки метод вернет null.
     */
    public static Element getElement(Element element, String tagName) {
        return NodeToElement(getNode(element, tagName));
    }

    /**
     * Получение элемента из первого дочернего узла из списка узлов, найденого
     * по имени тега в текущем елементе.
     *
     * @param element - текущий элемент
     * @param tagName - имя тега
     * @param n - номер узла
     * @return Element. В случае ошибки метод вернет null.
     */
    public static Element getElement(Element element, String tagName, Integer n) {
        return NodeToElement(getNode(element, tagName, n));
    }

    /**
     * Полученрие элемента из узла из набора узлов собранных по именам тегов из
     * текущего узла.
     *
     * @param node - текущий узел
     * @param tagNames - список имет тегов
     * @return Element. В случае ошибки метод вернет null.
     */
    public static Element getElement(Node node, String[] tagNames) {
        return NodeToElement(getNode(node, tagNames));
    }

    /**
     * Полученрие элемента из узла из набора узлов собранных по именам тегов из
     * текущего узла.
     *
     * @param element - текущий элемент
     * @param tagNames - список имет тегов
     * @return Element. В случае ошибки метод вернет null.
     */
    public static Element getElement(Element element, String[] tagNames) {
        return NodeToElement(getNode(element, tagNames));
    }

    /**
     * Значение n-го элемента из спсика найденного по имени тега в указанном
     * элементе.
     *
     * @param element - элемент
     * @param tagName - тег
     * @param n - номер элемента
     * @return String - значение данного элемента. В случае если элемент не
     * найден вернет пустую строку.
     */
    public static String getElementValue(Element element, String tagName, Integer n) {
        NodeList elementLst = element.getElementsByTagName(tagName);
        if (elementLst.item(n) != null) {
            NodeList elementData = ((Element) elementLst.item(n)).getChildNodes();
            if(elementData.item(0) != null) {
                return ((Node) elementData.item(0)).getNodeValue();
            }
        }
        return "";
    }

    /**
     * Значение первого элемента из спсика найденного по имени тега в указанном
     * элементе.
     *
     * @param element - элемент
     * @param tagName - тег
     * @return String - значение данного элемента.
     */
    public static String getElementValue(Element element, String tagName) {
        return getElementValue(element, tagName, 0);
    }
    
}
