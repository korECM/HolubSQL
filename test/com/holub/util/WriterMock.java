package com.holub.util;

import java.io.IOException;
import java.io.Writer;

public class WriterMock extends Writer {

    public StringBuilder out = new StringBuilder();

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
    }

    @Override
    public void write(String str) throws IOException {
        out.append(str);
    }

    public String getOutput() {
        return out.toString();
    }

    @Override
    public void flush() throws IOException {

    }

    @Override
    public void close() throws IOException {

    }
}
