package org.example;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

/**
 * Работник питстопа, меняет шину на прибывшей машине на своем месте
 */
@Log4j2
public class PitWorker extends Thread {

    //Место работника, он же номер колеса от 0 до 3
    private final int position;

    //Ссылка на сущность питстопа для связи
    private final PitStop pitStop;

    public PitWorker(int position, PitStop pitStop) {
        this.position = position;
        this.pitStop = pitStop;
    }

    @Override
    public void run() {
        while (!isInterrupted()) {
            F1Cars car = pitStop.getCar();

            //работник ждет машину на питстопе и меняет шину на своей позиции
            log.info("работник питстопа {} меняет колесо {} на авто под номером {}", this.getName(), position, car.getId());
            car.getWheel(position).replaceWheel();

            //работник сообщает о готовности
            log.info("работник питстопа {} закончил замену колеса {} на авто под номером {}", this.getName(), position, car.getId());
            pitStop.notifyComplete();
        }
    }
}
