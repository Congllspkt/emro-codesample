package smartsuite.app.sp.pro.po.repository;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import smartsuite.data.FloaterStream;
import smartsuite.mybatis.QueryFloaterStream;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;

@Service
public class SpPoConsortiumRepository {

    @Inject
    private SqlSession sqlSession;

    private static final String NAMESPACE = "sp-po-consortium-vd.";

    public Map<String,Object> findPoCs(Map<String, Object> param) {
        return sqlSession.selectOne(NAMESPACE + "findPoCs", param);
    }

    public List<Map<String,Object>> findListPoCsVd(Map<String, Object> param) {
        return  sqlSession.selectList(NAMESPACE + "findListPoCsVd", param);
    }

    public FloaterStream findListPoByCsVd(Map<String, Object> param) {
        return  new QueryFloaterStream(sqlSession, NAMESPACE + "findListPoByCsVd", param);
    }
}
