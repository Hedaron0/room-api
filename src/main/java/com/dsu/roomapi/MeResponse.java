package com.dsu.roomapi;

import java.util.List;

public record MeResponse(String username, List<String> roles) {
}
