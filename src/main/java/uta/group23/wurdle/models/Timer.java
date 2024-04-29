package uta.group23.wurdle.models;

public class Timer {
    
    public void startTimer() {
        try {
            
            // Sleep for 5 minutes
            Thread.sleep(5 * 60 * 1000);
            
            // After 5 minutes, notify timer ended
            timerCompleted();
        } catch (InterruptedException e) {
            // Handle interruption if needed
            System.out.println("Timer was interrupted");
            e.printStackTrace();
        }
    }

    private void timerCompleted() {
        // This method will be called when the timer completes
        System.out.println("Timer completed!");
    }
}
