package smartsuite.deploy;

import com.google.common.collect.Maps;
import org.apache.ibatis.ognl.Ognl;
import org.apache.ibatis.ognl.OgnlContext;
import org.apache.ibatis.ognl.OgnlException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.Map;

public class MultiDbXmlFile {
    public static void main(String[] args) {
        TargetDB targetDB = TargetDB.POSTGRESQL;
        String sourceFilePath = "/mnt/data/task-exception.xml";
        String destinationFilePath = "/mnt/data/after-task-exception.xml";

        try {
            modifyAndCopyXmlFile(sourceFilePath, destinationFilePath, targetDB.getName());
            System.out.println("패턴 제거 및 복사 완료!");
        } catch (IOException | ParserConfigurationException | SAXException | TransformerException e) {
            System.err.println("파일 처리 중 오류 발생: " + e.getMessage());
        }
    }

    private static void clearFileContents(String filePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("");
        }
    }

    public static String extractPreMapperContent(String sourceFilePath) throws IOException {
        StringBuilder preMapperContent = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(sourceFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().startsWith("<mapper")) {
                    break;
                }
                preMapperContent.append(line).append("\n");
            }
        }
        return preMapperContent.toString();
    }

    public static void prependPreMapperContent(String destinationFilePath, String preMapperContent) throws IOException {
        // 기존 파일의 내용을 읽어오기
        StringBuilder fileContents = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(destinationFilePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                fileContents.append(line).append("\n");
            }
        }

        // 앞 부분을 제거하고 preMapperContent를 추가한 내용을 생성
        String newFileContents = preMapperContent + fileContents.toString().replaceFirst("[\\s\\S]*?<mapper", "<mapper");

        // 새로운 내용을 파일에 쓰기
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(destinationFilePath))) {
            writer.write(newFileContents);
        }
    }

    public static void modifyXmlFile(String sourceFilePath, String destinationFilePath, String dbType)
            throws IOException, ParserConfigurationException, SAXException, TransformerException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(sourceFilePath));

        // XML 문서에서 모든 <if> 요소 가져오기
        NodeList ifNodes = doc.getElementsByTagName("if");

        OgnlContext context = new OgnlContext();
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("_databaseId", dbType);
        context.setRoot(parameters);

        // 각 <if> 요소에 대해 조건을 확인하고 삭제
        for (int i = ifNodes.getLength() - 1; i >= 0; i--) {
            Node ifNode = ifNodes.item(i);
            if (ifNode.getNodeType() == Node.ELEMENT_NODE) {
                Element ifElement = (Element) ifNode;
                String testAttribute = ifElement.getAttribute("test");

                // TARGET_DB와 일치하지 않는 경우 해당 요소와 주변 공백 노드 제거
                if (testAttribute.contains("_databaseId")) {
//                    if (!testAttribute.contains(dbType)) {
                        //"_databaseId != 'mssql'" 내용에 한해서만 체크 (24.09.09 jsha 테스트)
                        String expression = testAttribute;
                        Object ognlExpression = null;
                        try {
                            ognlExpression = Ognl.parseExpression(expression);
                            Boolean result = (Boolean) Ognl.getValue(ognlExpression, context, context.getRoot());
                            if(result) {
                                // 해당 dbType에 맞는 <if> 요소는 유지하고 외부 <if> 요소만 제거
                                Node parentNode = ifElement.getParentNode();
                                NodeList childNodes = ifElement.getChildNodes();
                                for (int j = 0; j < childNodes.getLength(); j++) {
                                    Node childNode = childNodes.item(j);
                                    parentNode.insertBefore(childNode.cloneNode(true), ifElement);
                                }
                            }
                            removeNodeWithSurroundingWhitespace(ifElement);
                        } catch(OgnlException e) {
                            throw new RuntimeException(e);
                        }

//                    } else {
//                        // 해당 dbType에 맞는 <if> 요소는 유지하고 외부 <if> 요소만 제거
//                        Node parentNode = ifElement.getParentNode();
//                        NodeList childNodes = ifElement.getChildNodes();
//                        for (int j = 0; j < childNodes.getLength(); j++) {
//                            Node childNode = childNodes.item(j);
//                            parentNode.insertBefore(childNode.cloneNode(true), ifElement);
//                        }
//                        removeNodeWithSurroundingWhitespace(ifElement);
//                    }
                }
            }
        }

        // XML을 문자열로 변환하여 저장
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));
        String xmlString = writer.getBuffer().toString();

        // 공백 줄 제거
        xmlString = removeEmptyLinesAroundIfStatements(xmlString);

        // 수정된 XML 내용을 파일에 쓰기
        try (BufferedWriter fileWriter = new BufferedWriter(new FileWriter(destinationFilePath))) {
            fileWriter.write(xmlString);
        }
    }

    private static void removeNodeWithSurroundingWhitespace(Node node) {
        Node parentNode = node.getParentNode();
        Node prevSibling = node.getPreviousSibling();
        Node nextSibling = node.getNextSibling();

        // Remove previous whitespace node if it exists
        if (prevSibling != null && prevSibling.getNodeType() == Node.TEXT_NODE) {
            Text prevText = (Text) prevSibling;
            String textContent = prevText.getTextContent().replaceAll("\\s+$", "");
            if (textContent.isEmpty()) {
                parentNode.removeChild(prevSibling);
            } else {
                prevText.setTextContent(textContent + " "); // Add a space at the end
            }
        }

        // Remove next whitespace node if it exists
        if (nextSibling != null && nextSibling.getNodeType() == Node.TEXT_NODE) {
            Text nextText = (Text) nextSibling;
            String textContent = nextText.getTextContent().replaceAll("^\\s+", "");
            if (textContent.isEmpty()) {
                parentNode.removeChild(nextSibling);
            } else {
                nextText.setTextContent(" " + textContent); // Add a space at the start
            }
        }

        // Remove the target node
        parentNode.removeChild(node);
    }

    private static String removeEmptyLinesAroundIfStatements(String xmlString) {
        // 공백 라인 제거
        return xmlString.replaceAll("(?m)^[ \t]*\r?\n", "");
    }

    public static void modifyAndCopyXmlFile(String sourceFilePath, String destinationFilePath, String dbType)
            throws IOException, ParserConfigurationException, SAXException, TransformerException {
        // Pre Mapper Content 추출
        String preMapperContent = MultiDbXmlFile.extractPreMapperContent(sourceFilePath);

        // XML 파일 수정
        MultiDbXmlFile.modifyXmlFile(sourceFilePath, destinationFilePath, dbType);

        // Pre Mapper 를 추가하여 파일 복사
        MultiDbXmlFile.prependPreMapperContent(destinationFilePath, preMapperContent);
    }
}