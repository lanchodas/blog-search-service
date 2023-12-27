package org.blog.database.configuration

import com.zaxxer.hikari.HikariDataSource
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.sql.DataSource

@Configuration
@EnableTransactionManagement
@EntityScan("org.blog.database.entity")
@EnableJpaRepositories("org.blog.database.repository")
internal class DataSourceConfiguration {

    /**
     * username, password는 application-database.yml에 하드코딩 되어 있지않고,
     * Vault 같은 KMS를 통해 가져온 값으로 설정하는 것을 가정합니다.
     */
    @Bean
    @ConfigurationProperties("spring.datasource.hikari")
    fun dataSource(properties: DataSourceProperties): DataSource {
        return HikariDataSource().apply {
            jdbcUrl = properties.url
            driverClassName = properties.driverClassName
            username = "username"
            password = "password"
        }
    }
}
