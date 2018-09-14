package scenarioTest.service;

import org.springframework.stereotype.Service;
import scenarioTest.model.TestTask;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class ScenarioTestServiceImpl implements ScenarioTestService{

    private final int THREAD_POOL_SIZE = 100;
    private final int BUFFER_QUEUE_SIZE = 2 * THREAD_POOL_SIZE;
    private final String TEST_LOGIN = "TestServiceLogin";
    private final String TEST_FLOW_ONE = "TestServiceFlowOne";
    private final String TEST_CANCEL_TICKET = "TestServiceCancel";
    private int taskCount = 0;

    private ThreadPoolExecutor threadPoolExecutor =  new  ThreadPoolExecutor(THREAD_POOL_SIZE, THREAD_POOL_SIZE,
                                      0L,TimeUnit.MILLISECONDS,
                                      new LinkedBlockingQueue<Runnable>(BUFFER_QUEUE_SIZE));

    @Override
    public void testLogin() {
        addTaskToThreadPool(TEST_LOGIN);
    }

    @Override
    public String shutDownThreadPool() {
        threadPoolExecutor.shutdown();
        return "The thread pool is shutdown, and the count of tasks executed is " + taskCount;
    }

    @Override
    public void testFlowOne() {
        addTaskToThreadPool(TEST_FLOW_ONE);
    }

    @Override
    public void testCancelTicket() {
        addTaskToThreadPool(TEST_CANCEL_TICKET);
    }

    @Override
    public String getCurrentTaskNumber() {
        return "The number of total tasks is " + taskCount + System.getProperty("line.separator")
                + "The number of completed task is " + threadPoolExecutor.getCompletedTaskCount() + System.getProperty("line.separator")
                + "The thread pool size is " + threadPoolExecutor.getPoolSize() + System.getProperty("line.separator")
                + "The number of Buffered task is " + threadPoolExecutor.getQueue().size();
    }

    private void addTaskToThreadPool(String taskName) {
        while (!(threadPoolExecutor.isShutdown() || threadPoolExecutor.isTerminated())) {

            try {
                if (threadPoolExecutor.getQueue().size() >= BUFFER_QUEUE_SIZE)
                {
                    // sleep 10 minutes
                    Thread.sleep(600000);
                }

                if (threadPoolExecutor.getPoolSize() <= THREAD_POOL_SIZE) {
                    TestTask testTask = new TestTask(taskName);
                    threadPoolExecutor.execute(testTask);
                    taskCount++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
