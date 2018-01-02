package text;

import org.apache.commons.io.FileUtils;
import utils.JsonUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RegexPrase {
    static String path = "C:\\Users\\script\\Desktop\\临床路径-初步处理\\临床路径\\txt-全\\";
    static List<String> icd10None = new LinkedList<>();
    static HashMap<String, Integer> titleNum = new HashMap<>();

    public static void count(String s) {
        if (titleNum.containsKey(s)) {
            titleNum.put(s, titleNum.get(s) + 1);
        } else {
            titleNum.put(s, 1);
        }
    }

    public static void main(String[] args) throws IOException {
        main();
//        praseOne("伤寒.docx.txt");
        FileUtils.writeLines(new File("icd10为空.txt"), "utf-8", icd10None);
        List<String> titleCount = new ArrayList<>();
        List<Map.Entry<String, Integer>> infoIds = new ArrayList<>(titleNum.entrySet());
        infoIds.sort(Comparator.comparingInt(Map.Entry::getValue));
        Collections.reverse(infoIds);
        infoIds.forEach(s -> titleCount.add(s.getKey() + "|" + s.getValue()));
        FileUtils.writeLines(new File("小标题计数统计.txt"), "utf-8", titleCount);
    }

    public static void main() {
        List<Map<String, Object>> jsonList = new ArrayList();
        List<File> list = Arrays.asList(new File(path).listFiles());
        System.out.println(list.size());
        list.forEach(file -> jsonList.add(praseOne(file.getName())));
        try {
            FileUtils.write(new File("result.json"), JsonUtils.toJsonBeautiful(jsonList), "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Map<String, Object> praseOne(String fileName) {
        String filePath = path + fileName;
        try {
            List<String> textList = FileUtils
                    .readLines(new File(filePath), "utf-8")
                    .stream()
                    .filter(line -> line.trim().length() != 0)
                    .collect(Collectors.toList());
            return getTitle(textList, fileName);
        } catch (Exception e) {
            System.err.println(fileName);
            e.printStackTrace();
            return null;
        }
    }

    private static Map<String, Object> getTitle(List<String> list, String fileName) {
        Map<String, Object> one = new HashMap<>();
        String name = list.get(0).trim().replace("临床路径", "");
        String time = regex(list.get(1).trim(), ".(\\d{4}).*");
        String icd10 = "";

        List<HashMap<String, String>> infoList = new LinkedList<>();
        String title = null;
        StringBuilder info = new StringBuilder();

        for (String aList : list) {
            if (regex(aList.trim(), "二(.*)路径表单").length() != 0) {
                break;
            }
            String title_temp = regex(aList.trim(), "[(（][一二三四五六七八九十][一二三四五六七八九十]{0,1}[）)](.*)")
                    .replaceAll("[。:!@#,.'，；‘【】：“《》？、]", "");
            String icd10_temp = regex(aList.trim(), ".*[（(]ICD-10[:：](.*)[）)]");
            if (icd10_temp.length() != 0) {
                icd10 = icd10_temp;
            }
            if (!(title_temp.length() == 0)) {
                if (!info.toString().contains(list.get(0).trim())) {
                    HashMap<String, String> oneInfo = new HashMap<>();
                    if (title.contains("标准住院日")) {
                        oneInfo.put("title", "标准住院日");
                        oneInfo.put("info", title + "\n" + info.toString().trim());
                        infoList.add(oneInfo);
                    } else {
                        oneInfo.put("title", title.trim());
                        oneInfo.put("info", info.toString().trim());
                        infoList.add(oneInfo);
                    }

                }
                info.setLength(0);
                title = title_temp;
            } else {
                info.append(aList).append("\n");
            }


        }
        HashMap<String, String> oneInfo = new HashMap<>();
        oneInfo.put("title", title.trim());
        oneInfo.put("info", info.toString().trim());
        infoList.add(oneInfo);
        if (icd10.length() == 0) {
            icd10None.add(fileName);
//            System.out.println(fileName + "\t" + name + "\t" + icd10);
//            JsonUtils.print(one);
        }
        infoList.forEach(map -> {
            count(map.get("title"));
        });
        one.put("name", name.trim());
        one.put("time", time.trim());
        one.put("icd10", icd10.trim());
        one.put("infoList", infoList);
        one.put("original_text", list.stream().collect(Collectors.joining("\n")));

//        JsonUtils.print(one);
        return one;
    }

    private static String regex(String str, String rex) {
        Pattern p = Pattern.compile(rex);
        Matcher m = p.matcher(str);
        ArrayList<String> strs = new ArrayList<>();
        while (m.find()) {
            strs.add(m.group(1));
        }
        return strs.stream().collect(Collectors.joining());
    }


}
