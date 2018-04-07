package com.github.filipmalczak.vent.helper.resolver;

import java.util.List;
import java.util.Map;

class Helper {
    private Helper() {
    }

    static ResolvedPath resolvePathPart(Map target, String pathPart){
        String member = pathPart;
        String indexing = "";
        int leftBracketIdx = member.indexOf('[');
        if (leftBracketIdx >= 0) {
            member = pathPart.substring(0, leftBracketIdx);
            indexing = pathPart.substring(leftBracketIdx);
        }
        if (!indexing.isEmpty()) {
            Object currentObject = target.get(member);
            // loop over all but last "[idx]" part
            while (indexing.lastIndexOf('[') > 0) {
                if (!(currentObject instanceof List))
                    throw new RuntimeException("Tried indexing on something else than a list!");
                List list = (List) currentObject;
                // assume that indexing[0] == '['
                int rightBracketIdx = indexing.indexOf(']');
                if (rightBracketIdx < 1) // none or at 0
                    throw new RuntimeException("Malformed indexing!");
                int currentIdx = Integer.valueOf(indexing.substring(1, rightBracketIdx));
                currentObject = list.get(currentIdx);
                indexing = indexing.substring(rightBracketIdx+1);
            }
            //fixme: next lines are just ugly copypaste
            if (!(currentObject instanceof List))
                throw new RuntimeException("Tried indexing on something else than a list!");
            List list = (List) currentObject;
            // assume that indexing[0] == '['
            int rightBracketIdx = indexing.indexOf(']');
            if (rightBracketIdx < 1) // none or at 0
                throw new RuntimeException("Malformed indexing!");
            int currentIdx = Integer.valueOf(indexing.substring(1, rightBracketIdx));
            return AtListIndex.from(list, currentIdx);
        }
        return MemberOf.from(target, member);
    }
}
