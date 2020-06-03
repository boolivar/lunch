package org.bool.lunch.scalecube;

import org.bool.lunch.LunchItem;
import org.bool.lunch.LunchRunner;
import org.bool.lunch.Lunched;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.*;

class LocalLunchServiceTest {

    LunchRunner lunchRunner = mock(LunchRunner.class);
    
    Process process = mock(Process.class);
    
    LunchItem lunchItem = new LunchItem("sh");
    
    Lunched lunched = new Lunched("test-pid", process, lunchItem);

    LocalLunchService service = new LocalLunchService(lunchRunner, Runnable::run);
    
    @Test
    void testLunch() {
        service.launch(lunchItem);
        
        then(lunchRunner)
                .should().launch(lunchItem);
    }
    
    @Test
    void testSubscriber() throws InterruptedException {
        Consumer<Lunched> consumer = mock(Consumer.class);
        Consumer<Throwable> errorConsumer = mock(Consumer.class);
        Runnable finalizer = mock(Runnable.class);
        
        given(process.waitFor())
                .willReturn(0);
        given(lunchRunner.launch(lunchItem))
                .willReturn(lunched);
        
        service.launch(lunchItem)
                .subscribe(consumer, errorConsumer, finalizer);
        
        then(consumer)
                .should().accept(lunched);
        then(errorConsumer)
                .shouldHaveNoInteractions();
        then(finalizer)
                .should().run();
    }
    
    @Test
    void testError() throws InterruptedException {
        Consumer<Lunched> consumer = mock(Consumer.class);
        Consumer<Throwable> errorConsumer = mock(Consumer.class);
        Runnable finalizer = mock(Runnable.class);
        ArgumentCaptor<ProcessTerminatedException> error = ArgumentCaptor.forClass(ProcessTerminatedException.class);
        
        given(process.waitFor())
                .willReturn(10);
        given(lunchRunner.launch(lunchItem))
                .willReturn(lunched);
        
        service.launch(lunchItem)
                .subscribe(consumer, errorConsumer);
        
        then(consumer)
                .should().accept(lunched);
        then(errorConsumer)
                .should().accept(error.capture());
        then(finalizer)
                .shouldHaveNoInteractions();
        
        assertEquals("test-pid", error.getValue().getPid());
        assertEquals(10, error.getValue().getExitCode());
    }
}