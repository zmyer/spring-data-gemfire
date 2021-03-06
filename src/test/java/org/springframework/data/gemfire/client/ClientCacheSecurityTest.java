/*
 * Copyright 2010-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.data.gemfire.client;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.gemfire.fork.ServerProcess;
import org.springframework.data.gemfire.process.ProcessExecutor;
import org.springframework.data.gemfire.process.ProcessWrapper;
import org.springframework.data.gemfire.test.support.FileSystemUtils;
import org.springframework.data.gemfire.test.support.ThreadUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.gemstone.gemfire.cache.CacheLoader;
import com.gemstone.gemfire.cache.CacheLoaderException;
import com.gemstone.gemfire.cache.LoaderHelper;
import com.gemstone.gemfire.cache.Region;

/**
 * The ClientCacheSecurityTest class is a test suite with test cases testing SSL configuration between a GemFire client
 * and server using the cluster-ssl-*  GemFire System properties.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringJUnit4ClassRunner
 * @since 1.7.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@SuppressWarnings("all")
public class ClientCacheSecurityTest {

	private static ProcessWrapper serverProcess;

	@Resource(name = "Example")
	private Region<String, String> example;

	@BeforeClass
	public static void setup() throws IOException {
		String serverName = "GemFireSecurityCacheServer";

		File serverWorkingDirectory = new File(FileSystemUtils.WORKING_DIRECTORY, serverName.toLowerCase());

		Assert.isTrue(serverWorkingDirectory.isDirectory() || serverWorkingDirectory.mkdirs());

		List<String> arguments = new ArrayList<String>();

		arguments.add(String.format("-Dgemfire.name=%1$s", serverName));
		arguments.add(String.format("-Djavax.net.ssl.keyStore=%1$s", System.getProperty("javax.net.ssl.keyStore")));
		arguments.add("/org/springframework/data/gemfire/client/ClientCacheSecurityTest-server-context.xml");

		serverProcess = ProcessExecutor.launch(serverWorkingDirectory, ServerProcess.class,
			arguments.toArray(new String[arguments.size()]));

		waitForServerStart(TimeUnit.SECONDS.toMillis(20));

		System.out.println("GemFire Cache Server Process for ClientCache Security should be running...");
	}

	private static void waitForServerStart(final long milliseconds) {
		ThreadUtils.timedWait(milliseconds, TimeUnit.MILLISECONDS.toMillis(500), new ThreadUtils.WaitCondition() {
			private File serverPidControlFile = new File(serverProcess.getWorkingDirectory(),
				ServerProcess.getServerProcessControlFilename());

			@Override public boolean waiting() {
				return !serverPidControlFile.isFile();
			}
		});
	}

	@AfterClass
	public static void tearDown() {
		serverProcess.shutdown();

		if (Boolean.valueOf(System.getProperty("spring.gemfire.fork.clean", Boolean.TRUE.toString()))) {
			org.springframework.util.FileSystemUtils.deleteRecursively(serverProcess.getWorkingDirectory());
		}
	}

	@Test
	public void exampleRegionGet() {
		assertThat(String.valueOf(example.get("TestKey")), is(equalTo("TestValue")));
	}

	@SuppressWarnings("unused")
	public static class TestCacheLoader implements CacheLoader<String, String> {

		@Override public String load(final LoaderHelper<String, String> helper) throws CacheLoaderException {
			return "TestValue";
		}

		@Override public void close() {
		}
	}

}
