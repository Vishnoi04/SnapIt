package com.snapit.service.impl;

import com.snapit.exception.WebPageFetchException;
import com.snapit.service.WebPageExtractionService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;

@Service
public class WebPageExtractionServiceImpl implements WebPageExtractionService {
    private static final int CONNECTION_TIMEOUT_MILLIS = 10_000;
    private static final int MAX_RESPONSE_BYTES = 5 * 1024 * 1024;
    private static final int MAX_REDIRECTS = 5;

    @Override
    public Document fetchWebPage(String url) {
        URI currentUri = parseAndValidateUri(url);
        try {
            for (int redirectCount = 0; redirectCount <= MAX_REDIRECTS; redirectCount++) {
                validatePublicDestination(currentUri);
                var response = Jsoup.connect(currentUri.toString())
                        .followRedirects(false)
                        .maxBodySize(MAX_RESPONSE_BYTES)
                        .timeout(CONNECTION_TIMEOUT_MILLIS)
                        .execute();

                if (response.statusCode() >= 300 && response.statusCode() < 400) {
                    if (redirectCount == MAX_REDIRECTS) {
                        throw new WebPageFetchException("Webpage exceeded the redirect limit.", null);
                    }
                    String location = response.header("Location");
                    if (location == null || location.isBlank()) {
                        throw new WebPageFetchException("Webpage returned an invalid redirect.", null);
                    }
                    currentUri = parseAndValidateUri(currentUri.resolve(location).toString());
                    continue;
                }
                if (response.statusCode() < 200 || response.statusCode() >= 300) {
                    throw new WebPageFetchException(
                            "Webpage returned HTTP status " + response.statusCode() + ".",
                            null
                    );
                }
                return response.parse();
            }
            throw new WebPageFetchException("Webpage exceeded the redirect limit.", null);
        } catch (IOException exception) {
            throw new WebPageFetchException(
                    "Unable to fetch webpage. Check the URL and try again later.",
                    exception
            );
        }
    }

    private URI parseAndValidateUri(String url) {
        try {
            URI uri = new URI(url).normalize();
            String scheme = uri.getScheme();
            String host = uri.getHost();
            if (host != null && host.startsWith("[") && host.endsWith("]")) {
                host = host.substring(1, host.length() - 1);
            }
            if (scheme == null
                    || !(scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))
                    || host == null
                    || uri.getUserInfo() != null
                    || uri.getPort() > 65535) {
                throw new IllegalArgumentException("URL must be a valid public HTTP or HTTPS address.");
            }
            return uri;
        } catch (URISyntaxException exception) {
            throw new IllegalArgumentException("URL must be a valid public HTTP or HTTPS address.", exception);
        }
    }

    private void validatePublicDestination(URI uri) throws IOException {
        String host = uri.getHost();
        if (host.startsWith("[") && host.endsWith("]")) {
            host = host.substring(1, host.length() - 1);
        }
        InetAddress[] addresses = InetAddress.getAllByName(host);
        if (addresses.length == 0) {
            throw new IOException("Unable to resolve webpage host.");
        }
        for (InetAddress address : addresses) {
            if (!isPublicAddress(address)) {
                throw new IllegalArgumentException("URL must resolve to a public IP address.");
            }
        }
    }

    private boolean isPublicAddress(InetAddress address) {
        if (address.isAnyLocalAddress()
                || address.isLoopbackAddress()
                || address.isLinkLocalAddress()
                || address.isSiteLocalAddress()
                || address.isMulticastAddress()) {
            return false;
        }

        byte[] bytes = address.getAddress();
        if (address instanceof Inet4Address) {
            int first = Byte.toUnsignedInt(bytes[0]);
            int second = Byte.toUnsignedInt(bytes[1]);
            int third = Byte.toUnsignedInt(bytes[2]);
            return first != 0
                    && !(first == 100 && second >= 64 && second <= 127)
                    && !(first == 192 && second == 0 && (third == 0 || third == 2))
                    && !(first == 198 && (second == 18
                    || second == 19
                    || second == 51 && third == 100))
                    && !(first == 203 && second == 0 && third == 113)
                    && first < 224;
        }
        if (address instanceof Inet6Address) {
            return (Byte.toUnsignedInt(bytes[0]) & 0xE0) == 0x20
                    && !(Byte.toUnsignedInt(bytes[0]) == 0x20
                    && Byte.toUnsignedInt(bytes[1]) == 0x01
                    && Byte.toUnsignedInt(bytes[2]) == 0x0D
                    && Byte.toUnsignedInt(bytes[3]) == 0xB8);
        }
        return false;
    }
}
