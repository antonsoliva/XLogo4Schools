/*
 * Copyright 2014 samuelcampos.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Changed by Marko Zivkovic: Introduced ThreadLocal variable for input.
 * Access is now thread safe, working on each thread individually (and thus requires to call all methods on each thread separately)
 */
package net.samuelcampos.usbdrivedectector.process;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author samuelcampos
 */
public class CommandLineExecutor implements Closeable {

    private static final Logger logger = LogManager.getLogger(CommandLineExecutor.class);

    private Process process = null;
    private final ThreadLocal<BufferedReader> input = new ThreadLocal<BufferedReader>();

    public void executeCommand(String command) throws IOException {
        if (logger.isTraceEnabled()) {
            logger.trace("Running command: " + command);
        }
        
        process = Runtime.getRuntime().exec(command);

        input.set(new BufferedReader(new InputStreamReader(process.getInputStream())));
    }
    
    public String readOutputLine() throws IOException {
        if(input == null)
            throw new IllegalStateException("You need to call 'executeCommand' method first");
        
         String outputLine = input.get().readLine();
         
         if(outputLine != null)
            return outputLine.trim();
         
         return null;
    }

    @Override
    public void close() throws IOException {
        if (input != null) {
            try {
                input.get().close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }

        if (process != null) {
            process.destroy();
        }

        input.set(null);
        process = null;
    }

}
