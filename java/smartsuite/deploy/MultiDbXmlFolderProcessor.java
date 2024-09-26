package smartsuite.deploy;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;

public class MultiDbXmlFolderProcessor {
    public static void main(String[] args) {
        System.out.println("XML 파일 처리 점검 ======================================");
        TargetDB targetDB = TargetDB.POSTGRESQL; // MAVEN dbVendor 불일치의 경우 하드코딩 (ex : (TargetDB 참조) oracle, mssql, mysql, postgre)
        String sourceFolderPath ="";// TEST 용도
        String destinationFolderPath = ""; // TEST 용도
        boolean isDeploy = false;

        String dbVendor = System.getProperty("dbVendor");
        if (dbVendor != null) {
            isDeploy = true;
            targetDB = getTargetDB(dbVendor, targetDB);
            String resourcePath = args[0];
            sourceFolderPath = resourcePath + "smartsuite/mappers"; // 차후 common 등 1개로 관리 후 배포 필요
            destinationFolderPath = resourcePath + "/temp/smartsuite/mappers";
        }
        System.out.println("sourceFolderPath:::" + sourceFolderPath);
        System.out.println("targetDb:::"+targetDB.getDatabaseType());

        try {
            long startTime = System.currentTimeMillis(); // 시작 시간 기록

            processXmlFiles(sourceFolderPath, destinationFolderPath, targetDB.getName());

            long endTime = System.currentTimeMillis(); // 종료 시간 기록
            long executionTime = endTime - startTime; // 실행 시간 계산
            System.out.println("XML 파일 처리가 완료되었습니다.");
            System.out.println("코드 실행 시간: " + executionTime + " 밀리초");

            // Deploy 인 경우 sourceFolderPath와 그 하위 폴더에 있는 파일과 폴더 모두 삭제
            if (isDeploy)
                deleteFolder(new File(sourceFolderPath));
        } catch (IOException e) {
            System.err.println("XML 파일 처리 중 오류 발생: " + e.getMessage());
        }
    }

    private static TargetDB getTargetDB(String envProfileId, TargetDB targetDB) {
        if (envProfileId != null) {
            for (TargetDB db : TargetDB.values()) {
                if (db.getName().equalsIgnoreCase(envProfileId)) {
                    return db;
                }
            }
        }
        // 환경 변수가 올바르지 않거나 설정되지 않은 경우 기본값으로 POSTGRESQL 선택
        return targetDB;
    }

    private static void processXmlFiles(String sourceFolderPath, String destinationFolderPath, String dbType) throws IOException {
        File sourceFolder = new File(sourceFolderPath);
        File destinationFolder = new File(destinationFolderPath);

        // 대상 폴더가 존재하지 않는 경우 생성
        if (!destinationFolder.exists()) {
            destinationFolder.mkdirs();
        }

        // 소스 폴더의 모든 파일 및 하위 폴더를 가져와서 처리
        File[] sourceFilesAndFolders = sourceFolder.listFiles();
        if (sourceFilesAndFolders != null) {
            for (File fileOrFolder : sourceFilesAndFolders) {
                if (fileOrFolder.isDirectory()) {
                    // 하위 폴더인 경우 재귀적으로 처리
                    String subSourceFolderPath = fileOrFolder.getAbsolutePath();
                    String subDestinationFolderPath = destinationFolderPath + File.separator + fileOrFolder.getName();
                    processXmlFiles(subSourceFolderPath, subDestinationFolderPath, dbType);
                } else if (fileOrFolder.getName().toLowerCase().endsWith(".xml")) {
                    // XML 파일인 경우 처리
                    String sourceFilePath = fileOrFolder.getAbsolutePath();
                    String destinationFilePath = destinationFolderPath + File.separator + fileOrFolder.getName();
                    modifyAndCopyXmlFile(sourceFilePath, destinationFilePath, dbType);
                }
            }
        }
    }

    private static void modifyAndCopyXmlFile(String sourceFilePath, String destinationFilePath, String dbType) throws IOException {
        // Pre Mapper Content 추출
        String preMapperContent = MultiDbXmlFile.extractPreMapperContent(sourceFilePath);

        // XML 파일 수정
        try {
            MultiDbXmlFile.modifyXmlFile(sourceFilePath, destinationFilePath, dbType);
        } catch (ParserConfigurationException | SAXException | TransformerException e) {
            throw new RuntimeException(e);
        }

        // Pre Mapper 를 추가하여 파일 복사
        MultiDbXmlFile.prependPreMapperContent(destinationFilePath, preMapperContent);
    }

    public static void deleteFolder(File folder) {
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        // 하위 폴더인 경우 해당 폴더를 삭제
                        deleteFolder(file);
                    } else {
                        // 파일인 경우 삭제
                        file.delete();
                    }
                }
            }
            // 모든 파일과 폴더를 삭제한 후에 해당 폴더 삭제
            folder.delete();
        } else {
            System.err.println("폴더가 존재하지 않거나 폴더가 아닙니다: " + folder.getPath());
        }
    }
}
