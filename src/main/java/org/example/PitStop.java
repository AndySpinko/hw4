package org.example;

import lombok.extern.log4j.Log4j2;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

@Log4j2
public class PitStop extends Thread {

    public static final int countWorkers = 4;

    PitWorker[] workers = new PitWorker[countWorkers];
    private final Semaphore pitlineSemaphore = new Semaphore(1);

    private final CyclicBarrier startWorkBarier;
    private final CyclicBarrier completeWorkBarier;
    private F1Cars currentCar;

    public PitStop() {
        startWorkBarier = new CyclicBarrier(countWorkers + 1);
        completeWorkBarier = new CyclicBarrier(countWorkers + 1);

        for (int i = 0; i < workers.length; i++) {
            workers[i] = new PitWorker(i, this);
            workers[i].start();
        }
    }

    public void pitline(F1Cars f1Cars) {
        try {
            pitlineSemaphore.acquire();
            log.info("Болид с номером {} заехал на питстоп {}, состояние колес {}%-{}%-{}%-{}%",
                    f1Cars.getId(), this.getName(),
                    f1Cars.getWheel(0).getStatus(),
                    f1Cars.getWheel(1).getStatus(),
                    f1Cars.getWheel(2).getStatus(),
                    f1Cars.getWheel(3).getStatus());
            currentCar = f1Cars;

            startWorkBarier.await();
            completeWorkBarier.await();
        } catch (InterruptedException | BrokenBarrierException interruptedException) {
            //
        } finally {
            log.info("Болид с номером {} выехал с питстопа {}, состояние колес {}%-{}%-{}%-{}%",
                    f1Cars.getId(), this.getName(),
                    f1Cars.getWheel(0).getStatus(),
                    f1Cars.getWheel(1).getStatus(),
                    f1Cars.getWheel(2).getStatus(),
                    f1Cars.getWheel(3).getStatus());
            pitlineSemaphore.release();
        }
    }


    @Override
    public void run() {
        while (!isInterrupted()) {
            //синхронизируем поступающие болиды и работников питстопа при необходимости
        }
    }

    public F1Cars getCar() {
        try {
            startWorkBarier.await();
        } catch (InterruptedException | BrokenBarrierException exception) {
            throw new RuntimeException(exception);
        }
        return currentCar;
    }

    public void notifyComplete() {
        try {
            completeWorkBarier.await();
        } catch (InterruptedException | BrokenBarrierException exception) {
            throw new RuntimeException(exception);
        }
    }
}
