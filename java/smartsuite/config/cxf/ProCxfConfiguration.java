package smartsuite.config.cxf;

import com.google.common.collect.Lists;
import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import smartsuite.app.cxfapi.po.impl.CxfApiPoWebserviceImpl;
import smartsuite.app.webservice.AuthenticationInterceptor;

import javax.xml.ws.Endpoint;

@Configuration
public class ProCxfConfiguration {
	
	private final Bus bus;

    private final AuthenticationInterceptor getAuthenticationInterceptor;

    public ProCxfConfiguration(Bus bus, AuthenticationInterceptor getAuthenticationInterceptor) {
        this.bus = bus;
        this.getAuthenticationInterceptor = getAuthenticationInterceptor;
    }


    @Bean
    public CxfApiPoWebserviceImpl getCxfApiPoWebservice() {
        return new CxfApiPoWebserviceImpl();
    }

    // 서비스 bean 추가 되는 곳
    @Bean("sendPo")
    public Endpoint sendPo() {
        EndpointImpl endpoint = new EndpointImpl(bus, this.getCxfApiPoWebservice());
        endpoint.setInInterceptors(Lists.newArrayList(getAuthenticationInterceptor));
        endpoint.publish("/sendPo");
        return endpoint;
    }
}
