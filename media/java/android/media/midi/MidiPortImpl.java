/*
 * Copyright (C) 2014 The Android Open Source Project
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
 */

package android.media.midi;

/**
 * This class contains utilities for socket communication between a
 * MidiInputPort and MidiOutputPort
 */
/* package */ class MidiPortImpl {
    private static final String TAG = "MidiPort";

    /**
     * Maximum size of a packet that can pass through our ParcelFileDescriptor.
     */
    public static final int MAX_PACKET_SIZE = 1024;

    /**
     * size of message timestamp in bytes
     */
    private static final int TIMESTAMP_SIZE = 8;

    /**
     * Maximum amount of MIDI data that can be included in a packet
     */
    public static final int MAX_PACKET_DATA_SIZE = MAX_PACKET_SIZE - TIMESTAMP_SIZE;

    /**
     * Utility function for packing a MIDI message to be sent through our ParcelFileDescriptor
     *
     * message byte array contains variable length MIDI message.
     * messageSize is size of variable length MIDI message
     * timestamp is message timestamp to pack
     * dest is buffer to pack into
     * returns size of packed message
     */
    public static int packMessage(byte[] message, int offset, int size, long timestamp,
            byte[] dest) {
        if (size + TIMESTAMP_SIZE > MAX_PACKET_SIZE) {
            size = MAX_PACKET_SIZE - TIMESTAMP_SIZE;
        }
        // message data goes first
        System.arraycopy(message, offset, dest, 0, size);

        // followed by timestamp
        for (int i = 0; i < TIMESTAMP_SIZE; i++) {
            dest[size++] = (byte)timestamp;
            timestamp >>= 8;
        }

        return size;
    }

    /**
     * Utility function for unpacking a MIDI message received from our ParcelFileDescriptor
     * returns the offset of the MIDI message in packed buffer
     */
    public static int getMessageOffset(byte[] buffer, int bufferLength) {
        // message is at the beginning
        return 0;
    }

    /**
     * Utility function for unpacking a MIDI message received from our ParcelFileDescriptor
     * returns size of MIDI data in packed buffer
     */
    public static int getMessageSize(byte[] buffer, int bufferLength) {
        // message length is total buffer length minus size of the timestamp
        return bufferLength - TIMESTAMP_SIZE;
    }

    /**
     * Utility function for unpacking a MIDI message received from our ParcelFileDescriptor
     * unpacks timestamp from packed buffer
     */
    public static long getMessageTimeStamp(byte[] buffer, int bufferLength) {
        // timestamp is at end of the packet
        int offset = bufferLength;
        long timestamp = 0;

        for (int i = 0; i < TIMESTAMP_SIZE; i++) {
            int b = (int)buffer[--offset] & 0xFF;
            timestamp = (timestamp << 8) | b;
        }
        return timestamp;
    }
}