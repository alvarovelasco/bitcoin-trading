package org.sonar.challenge.book.subscription.difford;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.websocket.DeploymentException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.verification.VerificationModeFactory;
import org.sonar.challenge.book.json.DiffOrderDecoder;
import org.sonar.challenge.exception.BitsoSubscribeException;
import org.sonar.challenge.exception.SonarChallengeException;
import org.sonar.challenge.websocket.BitsoSubscriber;

public class SubscribeDiffOrderCommandExecutorTest {

	@Mock
	private CreateDiffOrderForBookUpdateCommandContext context;
	
	@Mock
	private BitsoSubscriber<DiffOrderDecoder> bitsoSubscriber;
	
	private ExecutorService executorService;

	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		executorService = Executors.newSingleThreadExecutor();
	}
	
	@Test
	public void test_subscribeSuccess() throws SonarChallengeException, InterruptedException {
		new SubscribeDiffOrderCommandExecutor(bitsoSubscriber, context, executorService).execute();

		executorService.shutdown();
		executorService.awaitTermination(10, TimeUnit.SECONDS);
		
		verify(context, VerificationModeFactory.times(1)).setBitsoSubscriber(eq(bitsoSubscriber));
		verify(bitsoSubscriber, VerificationModeFactory.times(1)).subscribe();
	}
	
	@Test(expected=BitsoSubscribeException.class)
	public void test_subscribeException() throws SonarChallengeException, InterruptedException {
		doThrow(new BitsoSubscribeException(new DeploymentException("Exception"))).
			when(bitsoSubscriber).subscribe();
		
		new SubscribeDiffOrderCommandExecutor(bitsoSubscriber, context, executorService).execute();
		
		executorService.shutdown();
		executorService.awaitTermination(10, TimeUnit.SECONDS);
		
		verify(context, VerificationModeFactory.times(1)).setBitsoSubscriber(eq(bitsoSubscriber));
		verify(bitsoSubscriber, VerificationModeFactory.times(1)).subscribe();
	}
	
}
