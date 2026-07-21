package dev.emrullah.ai_panel.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CurrentDateTimeTool {

    @Tool(
            name = "get_current_time",
            description = """
                     Returns the current system date and current system time.
            
            Use this tool whenever the user asks:
            
            - What time is it?
            - Current time
            - Current date
            - Today's date
            - Date and time
            - System clock
            - Now
            - Current timestamp
            
            Always use this tool instead of estimating the current date or time.
            """
    )
    public String getCurrentTime() {
        return LocalDateTime.now().toString();
    }
}
