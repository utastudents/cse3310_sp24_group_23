package uta.group23.wurdle.models;

public class Timer 
{
    private boolean isRunning = false;

    public void startTimer() 
    {
        if (isRunning) 
        {
            System.out.println("Timer is already running");
            return;
        }

        isRunning = true;

        new Thread(() -> {
            try {
                
                //sleep for 5 minutes
                Thread.sleep(5 * 60 * 1000);

                //notify timer ended
                timerCompleted();

            } catch (InterruptedException e) {
                
                //handle interruption
                System.out.println("Timer was interrupted");
                e.printStackTrace();
            } finally {
                isRunning = false;
            }
        }).start();
    }

    private void timerCompleted() {
        // This method will be called when the timer completes
        System.out.println("Timer completed!");
    }
}
