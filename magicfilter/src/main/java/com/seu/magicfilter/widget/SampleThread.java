package com.seu.magicfilter.widget;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper thread class for java.lang.Thread. This class can be recycled. Once
 * Sample thread created, SampleThread.start() and SampleThread.stop() can be
 * used repeatedly.
 */
public class SampleThread {

    /**
     * Extended thread class for java.lang.Thread with flag for stopping thread.
     * This class cannot be recycled. SampleThread class will hide this recycle.
     */
    public class SafeStopThread extends Thread {

        public SafeStopThread() {
            /** Do not call super(). */
        }

        @Override
        public void run() {
            /** Loop start. */
            while (true) {
                long prevTime = System.currentTimeMillis() / 1000;

                synchronized (mRunnable) {
                    for (Runnable runnable : mRunnable)
                        runnable.run();
                }

                long postTime = System.currentTimeMillis() / 1000;
                long sleepTime = postTime - prevTime;

                /** Sleep at least 1 msec for avoiding ANR. */
                if (sleepTime >= mSleepTime)
                    sleepTime = 1;

                try {
                    Thread.sleep(sleepTime);
                } catch (Exception e) {
                    break;
                }
            }

            /** Loop end. */
        }

    }

    /**
     * Runnable done in thread loop.
     */
    private final List<Runnable> mRunnable;

    /**
     * Extended thread class.
     */
    private SafeStopThread mThread;

    /**
     * Sleep time each thread loop.
     */
    private final long mSleepTime;

    /**
     * @param FPS Frame per second for thread loop.
     */
    public SampleThread(int FPS) {
        mRunnable = new ArrayList<Runnable>();
        mSleepTime = FPS / 1000;
    }

    public void addRunnable(Runnable runnable) {
        synchronized (mRunnable) {
            mRunnable.add(runnable);
        }
    }

    public void removeRunnable(Runnable runnable) {
        synchronized (mRunnable) {
            mRunnable.remove(runnable);
        }
    }

    public void start() {
        mThread = new SafeStopThread();
        mThread.start();
    }

    public void stop() {
        /** Throw InterruptedException. */
        mThread.interrupt();
        mThread = null;
    }

}