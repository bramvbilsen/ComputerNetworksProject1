package project1;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Enum to store types of request such as GET, PUT...
 */
enum RequestTypes {
    GET, HEAD, PUT, POST
}

/**
 * Class to read and manage headers from a HTTP request.
 */
class Headers {

    /**
     * Raw string of read in headers.
     */
    private String headers;

    /**
     * Reads headers via inputStream and stores them.
     * 
     * @param inputStream Stream to read the headers from.
     * @throws IOException if an I/O error occurs.
     */
    Headers(InputStream inputStream) throws IOException {
        this.headers = this.readHeadersFromInputStream(inputStream);
    }

    /**
     * Reads the headers from the inputstream and stores them as a string.
     * 
     * @param inputStream stream
     * @param inputStream Stream to read the headers from.
     * @throws IOException if an I/O error occurs.
     */
    private String readHeadersFromInputStream(InputStream inputStream) throws IOException {

        String headersString = "";

        List<Character> last4Chars = new ArrayList<>();

        int currentByte;
        while ((currentByte = inputStream.read()) != -1) {
            char currentChar = (char) currentByte;

            last4Chars.add(currentChar);
            if (last4Chars.size() == 5) {
                last4Chars.remove(0);
                if (last4Chars.get(3) == '\n' && last4Chars.get(2) == '\r' && last4Chars.get(1) == '\n'
                        && last4Chars.get(0) == '\r') { // end of headers marked by /n/r/n/r.
                    break;
                }
            }

            headersString += currentChar;
        }
        System.out.println(headersString);
        return headersString;
    }

    /**
     * Gets the content type stored in the headers
     * 
     * @return the content type. If none found in headers, return UNKOWN.
     */
    public ContentTypes getContentType() {
        if (this.headers.indexOf("Content-Type: ") != -1) {
            String contentTypeCut = this.headers
                    .substring(this.headers.indexOf("Content-Type: ") + "Content-Type: ".length());
            int endOfCutIndex = contentTypeCut.indexOf('\r') < contentTypeCut.indexOf('\n')
                    ? contentTypeCut.indexOf('\r')
                    : contentTypeCut.indexOf('\n');
            contentTypeCut = contentTypeCut.substring(0, endOfCutIndex);
            if (contentTypeCut.contains("html")) {
                return ContentTypes.HTML;
            } else if (contentTypeCut.contains("image")) {
                return ContentTypes.IMAGE;
            } else if (contentTypeCut.contains("plain")) {
                return ContentTypes.PLAIN_TEXT;
            } else if (contentTypeCut.contains("javascript")) {
                return ContentTypes.SCRIPT;
            } else if (contentTypeCut.contains("css")) {
                return ContentTypes.STYLES;
            }
        }
        return ContentTypes.UNKOWN;
    }

    /**
     * Gets the content length stored in the headers.
     * 
     * @return the content length if found in headers. If not, we assume chunked
     *         body and return -1.
     */
    public int getContentLength() {
        if (this.headers.indexOf("Content-Length: ") != -1) {
            String contentLengthCut = this.headers
                    .substring(this.headers.indexOf("Content-Length: ") + "Content-Length: ".length());
            int endOfCutIndex = contentLengthCut.indexOf('\r') < contentLengthCut.indexOf('\n')
                    ? contentLengthCut.indexOf('\r')
                    : contentLengthCut.indexOf('\n');
            contentLengthCut = contentLengthCut.substring(0, endOfCutIndex);
            return Integer.parseInt(contentLengthCut);
        }
        return -1;
    }

    /**
     * !!!Only server side!!! Gets the request type for a request received by the
     * server.
     * 
     * @return the request type.
     */
    public RequestTypes getRequestType() {
        if (this.headers.startsWith("GET")) {
            return RequestTypes.GET;
        } else if (this.headers.startsWith("HEAD")) {
            return RequestTypes.HEAD;
        } else if (this.headers.startsWith("PUT")) {
            return RequestTypes.PUT;
        } else if (this.headers.startsWith("POST")) {
            return RequestTypes.POST;
        }
        return null;
    }

    /**
     * !!!Only server side!!! Gets the domain path from a request received by the
     * server.
     * 
     * @return the domain path
     */
    public String getDomainPath() {
        String firstHeaderLine = this.headers.substring(0, this.headers.indexOf('\r'));
        String pathStartCut = firstHeaderLine.substring(firstHeaderLine.indexOf("/"));
        return pathStartCut.substring(0, pathStartCut.indexOf(" ")).trim();
    }

    /**
     * Returns whether or not the connection should be closed after handling the
     * request from these headers.
     * 
     * @return true if connection should be closed, otherwise false.
     */
    public boolean connectionShouldClose() {
        return this.headers.indexOf("Connection: close") != -1;
    }

    @Override
    public String toString() {
        return this.headers;
    }
}