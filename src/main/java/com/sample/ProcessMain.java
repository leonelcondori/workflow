package com.sample;

/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;
import org.kie.test.util.db.PersistenceUtil;

public class ProcessMain {

	public static void main(String[] args) {

		Project project = new Project();

		while (true) {
			project.ejectTasks();
		}

//		KieServices ks = KieServices.Factory.get();
//		KieContainer kContainer = ks.getKieClasspathContainer();
//		KieBase kbase = kContainer.getKieBase("kbase");
//
//		RuntimeManager manager = createRuntimeManager(kbase);
//		RuntimeEngine engine = manager.getRuntimeEngine(null);
//		KieSession ksession = engine.getKieSession();
//		TaskService taskService = engine.getTaskService();
//
//		ksession.startProcess("com.sample.bpmn.hello");
//
//		// let john execute Task 1
//		List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
//		TaskSummary task = list.get(0);
//		System.out.println("John is executing task " + task.getName());
//		taskService.start(task.getId(), "john");
//		taskService.complete(task.getId(), "john", null);
//
//		// let mary execute Task 2
//		list = taskService.getTasksAssignedAsPotentialOwner("mary", "en-UK");
//		task = list.get(0);
//		System.out.println("Mary is executing task " + task.getName());
//		taskService.start(task.getId(), "mary");
//		taskService.complete(task.getId(), "mary", null);
//
//		manager.disposeRuntimeEngine(engine);
//		System.exit(0);
	}

}

class Project {
	private KieServices ks = KieServices.Factory.get();
	private KieContainer kContainer = ks.getKieClasspathContainer();
	private KieBase kbase = kContainer.getKieBase("kbase");
	private RuntimeManager manager;
	private KieSession ksession;
	private TaskService taskService;
	private RuntimeEngine engine;
	private Scanner sc = new Scanner(System.in);

	public Project() {
		manager = createRuntimeManager(kbase);
		engine = manager.getRuntimeEngine(null);
		ksession = engine.getKieSession();
		taskService = engine.getTaskService();
		ksession.startProcess("com.sample.bpmn.hello");

//		manager.disposeRuntimeEngine(engine);
//		System.exit(0);

	}

	public void ejectTasks() {
		// Collection<ProcessInstance> instances = ksession.getProcessInstances();
		System.out.print("Type your name: ");
		String name = sc.nextLine();
		List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner(name, "en-UK");
		if (!list.isEmpty()) {
			System.out.printf("\nYour tasks are: \n");
			for (TaskSummary task : list) {
				System.out.println(task.toString());
			}

			System.out.print("Type task id: ");
			int idtask = sc.nextInt();
			TaskSummary task = list.get(idtask);
			System.out.print("do ?");
			String resp = sc.next();

			if (resp.equalsIgnoreCase("si")) {
				System.out.println(name + " is executing task " + task.getName());
				taskService.start(task.getId(), name);
				taskService.complete(task.getId(), name, null);

				System.out.println("The task has been completed");
			}
		} else {
			System.out.println("you haven't taks");
		}
	}

	private static RuntimeManager createRuntimeManager(KieBase kbase) {
		Properties properties = new Properties();
		properties.put("driverClassName", "org.h2.Driver");
		properties.put("className", "org.h2.jdbcx.JdbcDataSource");
		properties.put("user", "sa");
		properties.put("password", "");
		properties.put("url", "jdbc:h2:tcp://localhost/~/jbpm-db");
		properties.put("datasourceName", "jdbc/jbpm-ds");
		PersistenceUtil.setupPoolingDataSource(properties);
		Map<String, String> map = new HashMap<String, String>();
		map.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");

		EntityManagerFactory emf = Persistence.createEntityManagerFactory("org.jbpm.persistence.jpa");
		RuntimeEnvironmentBuilder builder = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
				.entityManagerFactory(emf).knowledgeBase(kbase);
		return RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(builder.get(), "com.sample:example:1.0");
	}
}