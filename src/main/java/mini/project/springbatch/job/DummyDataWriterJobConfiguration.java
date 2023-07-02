package mini.project.springbatch.job;

import lombok.RequiredArgsConstructor;
import mini.project.springbatch.entity.User;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.util.Random;

@Configuration
@RequiredArgsConstructor
public class DummyDataWriterJobConfiguration {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    private final JobOperator jobOperator;

    private static final int chunkSize = 20;
    private int i = 0;

    @Bean
    public Job dummyDataWriterJob() {
        return jobBuilderFactory.get("dummyWriterJob")
                .start(dummyDataWriterStep())
                .next(limitCheckStep())
                .on("FAILED")
                .end()
                .end()
                .build();
    }

    @Bean
    public Step dummyDataWriterStep() {
        return stepBuilderFactory.get("dummyWriterStep")
                .<User, User>chunk(chunkSize)
                .reader(dummyDataWriterReader())
                .writer(dummyDataWriter())
                .build();
    }

    @Bean
    public Step limitCheckStep() {
        return stepBuilderFactory.get("limitStep")
                .tasklet((contribution, chunkContext) -> {
                            i++;
                            if (i == 10) {
                                contribution.setExitStatus(ExitStatus.FAILED);
                                return RepeatStatus.FINISHED;
                            }
                            return null;

                        }
                )
                .build();
    }

    @Bean
    public ItemReader<User> dummyDataWriterReader() {
        return () -> User.of(getRandomName(), getRandomName(), getRandomMale());
    }

    @Bean
    public JpaItemWriter<User> dummyDataWriter() {
        JpaItemWriter<User> jpaItemWriter = new JpaItemWriter<>();
        jpaItemWriter.setEntityManagerFactory(entityManagerFactory);
        return jpaItemWriter;
    }

    private static String getRandomName() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();
        return random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    private static boolean getRandomMale() {

        Random random = new Random();
        return random.nextInt(2) == 0;
    }

}
