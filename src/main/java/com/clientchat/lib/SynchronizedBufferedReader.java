package com.clientchat.lib;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SynchronizedBufferedReader {
    private final BufferedReader reader;
    private final Lock lock;

    public SynchronizedBufferedReader(BufferedReader reader) {
        this.reader = reader;
        this.lock = new ReentrantLock();
    
    }

    public String readLine() throws IOException {
        lock.lock();
        try {
            return reader.readLine();
        } finally {
            lock.unlock();
        }
    }

    public int read() throws IOException {
        lock.lock();
        try {
            return reader.read();
        } finally {
            lock.unlock();
        }
    }

    public void close() throws IOException {
        lock.lock();
        try {
            reader.close();
        } finally {
            lock.unlock();
        }
    }
} 