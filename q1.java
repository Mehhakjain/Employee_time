import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

class EmployeeRecord {
    String name;
    String position;
    Date date;
    Date endTime;

    public EmployeeRecord(String name, String position, Date date, Date endTime) {
        this.name = name;
        this.position = position;
        this.date = date;
        this.endTime = endTime;
    }
}

public class EmployeeAnalyzer {
    public static void main(String[] args) {
        String filename = "Assignment_Timecard.xlsx - Sheet1.csv"; // Replace with your input file name
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<EmployeeRecord> records = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    String name = parts[0];
                    String position = parts[1];
                    Date date = dateFormat.parse(parts[2]);
                    Date endTime = dateFormat.parse(parts[3]);
                    records.add(new EmployeeRecord(name, position, date, endTime));
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return;
        }

        // Sorting records by date
        records.sort(Comparator.comparing(record -> record.date));

        // Iterate over the records
        for (int i = 0; i < records.size(); i++) {
            EmployeeRecord currentRecord = records.get(i);
            String name = currentRecord.name;
            String position = currentRecord.position;
            Date currentDate = currentRecord.date;

            // Check for 7 consecutive days
            int consecutiveDays = 1; // The current day counts as one
            for (int j = i + 1; j < records.size(); j++) {
                EmployeeRecord nextRecord = records.get(j);
                long daysDifference = (nextRecord.date.getTime() - currentDate.getTime()) / (1000 * 60 * 60 * 24);
                if (daysDifference == consecutiveDays) {
                    consecutiveDays++;
                    if (consecutiveDays == 7) {
                        System.out.println(name + " (" + position + ") has worked for 7 consecutive days.");
                        break; // No need to continue checking
                    }
                } else {
                    break; // Days are not consecutive, stop checking
                }
            }

            // Check for less than 10 hours but greater than 1 hour between shifts
            if (i < records.size() - 1) {
                EmployeeRecord nextRecord = records.get(i + 1);
                long hoursDifference = (nextRecord.date.getTime() - currentRecord.endTime.getTime()) / (1000 * 60 * 60);
                if (hoursDifference > 1 && hoursDifference < 10) {
                    System.out.println(name + " (" + position + ") has less than 10 hours but greater than 1 hour between shifts.");
                }
            }

            // Check for more than 14 hours in a single shift
            long shiftHours = (currentRecord.endTime.getTime() - currentRecord.date.getTime()) / (1000 * 60 * 60);
            if (shiftHours > 14) {
                System.out.println(name + " (" + position + ") has worked for more than 14 hours in a single shift.");
            }
        }
    }
}
