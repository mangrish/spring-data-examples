/*
 * Copyright 2015-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package example.springdata.neo4j;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.ogm.drivers.embedded.driver.EmbeddedDriver;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.transaction.Neo4jTransactionManager;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

/**
 * Simple integration test demonstrating the use of the ActorRepository
 *
 * @author Luanne Misquitta
 * @author Oliver Gierke
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@Transactional
public class ActorRepositoryIntegrationTest {

	@Configuration
	@EnableTransactionManagement
	@EnableNeo4jRepositories
	static class ExampleConfig {

		@Bean
		SessionFactory getSessionFactory() {

			org.neo4j.ogm.config.Configuration configuration = new org.neo4j.ogm.config.Configuration();
			configuration.driverConfiguration().setDriverClassName(EmbeddedDriver.class.getName());

			return new SessionFactory(configuration, "example.springdata.neo4j");
		}

		@Bean
		Neo4jTransactionManager transactionManager(SessionFactory factory) {
			return new Neo4jTransactionManager(factory);
		}
	}

	@Autowired ActorRepository actorRepository;

	/**
	 * @see #131
	 */
	@Test
	public void shouldBeAbleToSaveAndLoadActor() {

		Movie goblet = new Movie("Harry Potter and the Goblet of Fire");

		Actor daniel = new Actor("Daniel Radcliffe");
		daniel.actedIn(goblet, "Harry Potter");

		actorRepository.save(daniel); // saves the actor and the movie

		// Look up the actor by the primary index.
		Actor actor = actorRepository.findOne(daniel.getId());

		assertThat(actor, is(notNullValue()));
		assertThat(actor.getName(), is(daniel.getName()));
		assertThat(actor.getRoles(), hasSize(1));

		Role role = actor.getRoles().iterator().next();

		assertThat(role.getRole(), is("Harry Potter"));
	}
}
