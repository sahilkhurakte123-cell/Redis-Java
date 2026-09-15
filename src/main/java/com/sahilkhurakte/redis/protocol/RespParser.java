package com.sahilkhurakte.redis.protocol;

import java.io.IOException;
import java.io.InputStream;

public class RespParser {

    // Parses one RESP array-of-bulk-strings command off the stream.
    // Returns null if the client disconnected before sending anything.
    public String[] parseCommand(InputStream in) throws IOException {
        int firstByte = in.read();
        if (firstByte == -1) {
            return null; // stream closed, no more commands coming
        }
        if (firstByte != '*') {
            throw new IOException("expected '*', got: " + (char) firstByte);
        }

        int numArgs = Integer.parseInt(readLine(in));

        String[] args = new String[numArgs];
        for (int i = 0; i < numArgs; i++) {
            int typeByte = in.read();
            if (typeByte != '$') {
                throw new IOException("expected '$', got: " + (char) typeByte);
            }
            int len = Integer.parseInt(readLine(in));
            args[i] = readBulkString(in, len);
        }
        return args;
    }

    // Reads raw bytes one at a time until it sees the two-byte sequence \r\n,
    // and returns everything before it as a String. This is the byte-level

    // let a buffered reader get ahead of us before we know a bulk string's
    // exact length.
    private String readLine(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        int prev = -1;
        int curr;
        while ((curr = in.read()) != -1) {
            if (prev == '\r' && curr == '\n') {
                sb.setLength(sb.length() - 1); // drop the \r we already appended
                break;
            }
            sb.append((char) curr);
            prev = curr;
        }
        return sb.toString();
    }

    // Reads EXACTLY 'len' bytes for a bulk string's body, then consumes the
    // trailing \r\n that always follows it in the protocol.
    private String readBulkString(InputStream in, int len) throws IOException {
        byte[] buf = new byte[len];
        int totalRead = 0;
        // in.read() can return fewer bytes than you asked for (the short-read
        // gotcha from earlier) — loop until you've actually got all 'len' bytes.
        while (totalRead < len) {
            int n = in.read(buf, totalRead, len - totalRead);
            if (n == -1) {
                throw new IOException("stream closed mid bulk-string");
            }
            totalRead += n;
        }
        in.read(); // consume \r
        in.read(); // consume \n
        return new String(buf);
    }
}