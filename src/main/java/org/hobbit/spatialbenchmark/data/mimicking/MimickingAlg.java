/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.hobbit.spatialbenchmark.data.mimicking;

import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.ShutdownSignalException;
import java.io.IOException;
import org.hobbit.core.Constants;
import org.hobbit.core.components.RabbitQueueFactory;
import org.hobbit.core.data.RabbitQueue;
import org.hobbit.core.rabbit.SimpleFileReceiver;

/**
 *
 * @author jsaveta
 */
public class MimickingAlg implements RabbitQueueFactory {

    public MimickingAlg() throws IOException, ShutdownSignalException, ConsumerCancelledException, InterruptedException {
        SimpleFileReceiver receiver = SimpleFileReceiver.create(this, Constants.DATA_QUEUE_NAME_KEY);
        String[] receivedFiles = receiver.receiveData("./datasets/");
//posa data mporo na paro apo do px?
    }

    @Override
    public RabbitQueue createDefaultRabbitQueue(String string) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
}
