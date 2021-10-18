package dgac.fragmentos.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.beans.factory.annotation.Autowired;

@Configuration
@EnableTransactionManagement
public class DbConfiguration {

	@Bean(name = "dataSource")
	@Profile("prod")
	public DataSource dataSource() {
		DriverManagerDataSource datasource = new DriverManagerDataSource();
		datasource.setDriverClassName("safe-holder");
		datasource.setUrl("safe-holder");
		datasource.setUsername("safe-holder");
		datasource.setPassword("safe-holder");
		return datasource;
	}

	@Bean(name = "dataSource")
	@Profile("dev")
	public DataSource devDataSource() {
		DriverManagerDataSource datasource = new DriverManagerDataSource();
		datasource.setDriverClassName("safe-holder");
		datasource.setUrl("safe-holder");
		datasource.setUsername("safe-holder");
		datasource.setPassword("safe-holder");
		return datasource;
	}

}
