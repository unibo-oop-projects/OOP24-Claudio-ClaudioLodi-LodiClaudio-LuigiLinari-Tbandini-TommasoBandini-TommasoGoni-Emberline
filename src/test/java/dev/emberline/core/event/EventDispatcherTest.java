package dev.emberline.core.event;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;

class EventDispatcherTest {

    private static final List<String> caughtEvents = new ArrayList<>();
    private final TestListener listener1 = new TestListener("Listener1");
    private final TestListener listener2 = new TestListener("Listener2");
    private final SubTestListener subListener1 = new SubTestListener("SubListener1");
    private final SubTestListener subListener2 = new SubTestListener("SubListener2");

    private static class TestEvent extends EventObject {
        private final String message;
        private TestEvent(Object source, String message) {
            super(source);
            this.message = message;
        }
        private String getMessage() {
            return message;
        }
    }

    private static class SubTestEvent extends TestEvent {
        private SubTestEvent(Object source, String message) {
            super(source, message);
        }
        private String getMessage() {
            return "[SUB]" + super.getMessage();
        }
    }

    private record TestListener(String name) implements EventListener {
        @EventHandler
        public void onTestEvent(TestEvent event) {
            caughtEvents.add(name + ": " + event.getMessage());
        }
    }

    private record SubTestListener(String name) implements EventListener {
        @EventHandler
        public void onSubTestEvent(SubTestEvent event) {
            caughtEvents.add(name + ": " + event.getMessage());
        }
    }

    private record InvalidListenerParameterType(String name) implements EventListener {
        @EventHandler
        public void onInvalidEvent(String event) {
            Assertions.fail("This method should not be called as it does not handle an EventObject");
        }
    }

    private record InvalidListenerReturnType(String name) implements EventListener {
        @EventHandler
        public int onInvalidEvent(TestEvent event) {
            Assertions.fail("This method should not be called as it does not return void");
            return 0;
        }
    }

    private record InvalidListenerTooManyParameters(String name) implements EventListener {
        @EventHandler
        public void onInvalidEvent(TestEvent event, String extraParam) {
            Assertions.fail("This method should not be called as it has too many parameters");
        }
    }

    @Test
    void testBasicEventDispatching() {
        EventDispatcher dispatcher = new EventDispatcher();

        // Dispatching an event before any listeners are registered
        dispatcher.dispatchEvent(new TestEvent(this, "Event before listeners"));
        Assertions.assertTrue(caughtEvents.isEmpty(), "No listeners should be registered yet");

        // Every listener is registered, sub listeners should not catch the event
        dispatcher.registerListener(listener1);
        dispatcher.registerListener(listener2);
        dispatcher.registerListener(subListener1);
        dispatcher.registerListener(subListener2);
        dispatcher.dispatchEvent(new TestEvent(this, "Test Event 1"));

        List<String> expectedEvents = List.of(
            "Listener1: Test Event 1",
            "Listener2: Test Event 1"
        );
        org.assertj.core.api.Assertions.assertThat(caughtEvents)
                .withFailMessage("Only listener1 and listener2 should catch the event")
                .containsExactlyInAnyOrderElementsOf(expectedEvents);
        caughtEvents.clear();

        // Every listener should catch the sub event
        dispatcher.dispatchEvent(new SubTestEvent(this, "Sub Test Event 1"));
        expectedEvents = List.of(
            "Listener1: Sub Test Event 1",
            "Listener2: Sub Test Event 1",
            "SubListener1: [SUB]Sub Test Event 1",
            "SubListener2: [SUB]Sub Test Event 1"
        );
        org.assertj.core.api.Assertions.assertThat(caughtEvents)
                .withFailMessage("All listeners should catch the sub event\n" +
                        "Expected: " + expectedEvents + "\n" +
                        "Actual: " + caughtEvents)
                .containsExactlyInAnyOrderElementsOf(expectedEvents);
        caughtEvents.clear();

        // Unregistering a listener and checking if it no longer receives events
        dispatcher.unregisterListener(listener1);
        dispatcher.dispatchEvent(new SubTestEvent(this, "Sub Test Event 2"));
        expectedEvents = List.of(
            "Listener2: Sub Test Event 2",
            "SubListener1: [SUB]Sub Test Event 2",
            "SubListener2: [SUB]Sub Test Event 2"
        );
        System.out.println("Caught events: " + caughtEvents);

        org.assertj.core.api.Assertions.assertThat(caughtEvents)
                .withFailMessage("Listener1 should not catch the event after being unregistered")
                .containsExactlyInAnyOrderElementsOf(expectedEvents);
        caughtEvents.clear();

        // Unregistering all listeners and checking if no events are caught
        dispatcher.unregisterListener(listener2);
        dispatcher.unregisterListener(subListener1);
        dispatcher.unregisterListener(subListener2);
        dispatcher.dispatchEvent(new TestEvent(this, "Test Event 3"));
        Assertions.assertTrue(caughtEvents.isEmpty(), "No listeners should be registered, so no events should be caught");
    }

    @Test
    void testEventDispatchingWithNullEvent() {
        EventDispatcher dispatcher = new EventDispatcher();
        Assertions.assertThrows(IllegalArgumentException.class, () -> dispatcher.dispatchEvent(null), "Dispatching a null event should throw IllegalArgumentException");
    }

    @Test
    void testRegisterNullListener() {
        EventDispatcher dispatcher = new EventDispatcher();
        Assertions.assertThrows(IllegalArgumentException.class, () -> dispatcher.registerListener(null), "Registering a null listener should throw IllegalArgumentException");
    }

    @Test
    void testUnregisterNullListener() {
        EventDispatcher dispatcher = new EventDispatcher();
        Assertions.assertThrows(IllegalArgumentException.class, () -> dispatcher.unregisterListener(null), "Unregistering a null listener should throw IllegalArgumentException");
    }

    @Test
    void testUnregisteringNonExistentListener() {
        EventDispatcher dispatcher = new EventDispatcher();
        TestListener nonExistentListener = new TestListener("NonExistent");
        Assertions.assertDoesNotThrow(() -> dispatcher.unregisterListener(nonExistentListener), "Unregistering a non-existent listener should not throw an exception");
    }

    @Test
    void testInvalidListenerParameterType() {
        EventDispatcher dispatcher = new EventDispatcher();
        InvalidListenerParameterType invalidListener = new InvalidListenerParameterType("InvalidListener");
        Assertions.assertThrows(InvalidEventHandlerException.class, () -> dispatcher.registerListener(invalidListener), "Registering a listener with an invalid parameter type should throw IllegalArgumentException");
    }

    @Test
    void testInvalidListenerTooManyParameters() {
        EventDispatcher dispatcher = new EventDispatcher();
        InvalidListenerTooManyParameters invalidListener = new InvalidListenerTooManyParameters("InvalidListener");
        Assertions.assertThrows(InvalidEventHandlerException.class, () -> dispatcher.registerListener(invalidListener), "Registering a listener with too many parameters should throw IllegalArgumentException");
    }
}