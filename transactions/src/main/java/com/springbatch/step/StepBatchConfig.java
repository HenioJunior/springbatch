package com.springbatch.step;


import com.springbatch.domain.Pessoa;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;


@Configuration
public class StepBatchConfig {

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    @Qualifier("transactionManagerApp")
    private PlatformTransactionManager transactionManager;

    @Bean
    public Step step(ItemReader<Pessoa> reader, ItemWriter<Pessoa> writer) {
        return stepBuilderFactory
                .get("step")
                .<Pessoa, Pessoa>chunk(200)
                .reader(reader)
                .writer(writer)
                .transactionManager(transactionManager)
                .build();
    }

    @Bean
    public ItemReader reader() {
        return new FlatFileItemReaderBuilder<Pessoa>()
                .name("reader")
                .resource(new FileSystemResource("files/pessoas.csv"))
                .comments("--")
                .delimited()
                .names("nome", "email", "dataNascimento", "idade", "id")
                .targetType(Pessoa.class)
                .build();
    }

    @Bean
    public ItemWriter<Pessoa> writer(@Qualifier("appDS") DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Pessoa>()
                .dataSource(dataSource)
                .sql("INSERT INTO pessoa (id, nome, email, data_nascimento, idade) " +
                        "VALUES (:id, :nome, :email, :dataNascimento, :idade)")
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .build();
    }
}
