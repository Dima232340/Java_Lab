package Trie;

import java.util.*;

public class Trie {

    // Узел префиксного дерева
    private static class TrieNode {
        private Map<Character, TrieNode> children;
        private boolean isEndOfWord;

        public TrieNode() {
            this.children = new HashMap<>();
            this.isEndOfWord = false;
        }
    }

    private final TrieNode root;

    public Trie() {
        this.root = new TrieNode();
    }

    /**
     * Вставка слова в префиксное дерево
     * 
     * @param word слово для вставки
     */
    public void insert(String word) {
        if (word == null || word.isEmpty()) {
            return;
        }

        TrieNode current = root;
        for (char c : word.toCharArray()) {
            current.children.putIfAbsent(c, new TrieNode());
            current = current.children.get(c);
        }
        current.isEndOfWord = true;
    }

    /**
     * Проверка наличия слова в дереве
     * 
     * @param word слово для проверки
     * @return true если слово существует, false в противном случае
     */
    public boolean contains(String word) {
        if (word == null || word.isEmpty()) {
            return false;
        }

        TrieNode node = findNode(word);
        return node != null && node.isEndOfWord;
    }

    /**
     * Проверка существования слов с данным префиксом
     * 
     * @param prefix префикс для проверки
     * @return true если есть слова с таким префиксом, false в противном случае
     */
    public boolean startsWith(String prefix) {
        if (prefix == null) {
            return false;
        }

        return findNode(prefix) != null;
    }

    /**
     * Получение всех слов по префиксу
     * 
     * @param prefix префикс для поиска
     * @return список всех слов, начинающихся с данного префикса
     */
    public List<String> getByPrefix(String prefix) {
        List<String> result = new ArrayList<>();
        if (prefix == null) {
            return result;
        }

        TrieNode node = findNode(prefix);
        if (node != null) {
            collectWords(node, prefix, result);
        }

        return result;
    }

    /**
     * Вспомогательный метод для поиска узла по строке
     * 
     * @param str строка для поиска
     * @return узел, соответствующий строке, или null если не найден
     */
    private TrieNode findNode(String str) {
        TrieNode current = root;
        for (char c : str.toCharArray()) {
            if (!current.children.containsKey(c)) {
                return null;
            }
            current = current.children.get(c);
        }
        return current;
    }

    /**
     * Рекурсивный сбор всех слов из поддерева
     * 
     * @param node          текущий узел
     * @param currentPrefix текущий префикс
     * @param result        список для сохранения результатов
     */
    private void collectWords(TrieNode node, String currentPrefix, List<String> result) {
        if (node.isEndOfWord) {
            result.add(currentPrefix);
        }

        for (Map.Entry<Character, TrieNode> entry : node.children.entrySet()) {
            collectWords(entry.getValue(), currentPrefix + entry.getKey(), result);
        }
    }

    /**
     * Удаление слова из дерева
     * 
     * @param word слово для удаления
     * @return true если слово было удалено, false если слово не найдено
     */
    public boolean remove(String word) {
        if (word == null || word.isEmpty()) {
            return false;
        }

        // Сначала проверяем, существует ли слово
        if (!contains(word)) {
            return false;
        }

        removeRecursive(root, word, 0);
        return true;
    }

    private void removeRecursive(TrieNode current, String word, int index) {
        if (index == word.length()) {
            // Просто снимаем флаг конца слова
            current.isEndOfWord = false;
            return;
        }

        char ch = word.charAt(index);
        TrieNode child = current.children.get(ch);

        if (child != null) {
            removeRecursive(child, word, index + 1);

            // Если дочерний узел пустой и не является концом слова, удаляем его
            if (child.children.isEmpty() && !child.isEndOfWord) {
                current.children.remove(ch);
            }
        }
    }

    /**
     * Получение количества слов в дереве
     * 
     * @return количество слов
     */
    public int size() {
        return countWords(root);
    }

    private int countWords(TrieNode node) {
        int count = 0;
        if (node.isEndOfWord) {
            count++;
        }

        for (TrieNode child : node.children.values()) {
            count += countWords(child);
        }

        return count;
    }

    /**
     * Проверка, пусто ли дерево
     * 
     * @return true если дерево пустое, false в противном случае
     */
    public boolean isEmpty() {
        return root.children.isEmpty();
    }

    @Override
    public String toString() {
        List<String> allWords = getByPrefix("");
        return "Trie{words=" + allWords + "}";
    }

    // Пример использования
    public static void main(String[] args) {
        Trie trie = new Trie();

        // Вставка слов
        trie.insert("apple");
        trie.insert("app");
        trie.insert("application");
        trie.insert("banana");
        trie.insert("band");
        trie.insert("cat");

        // Проверка наличия слов
        System.out.println("Contains 'apple': " + trie.contains("apple")); // true
        System.out.println("Contains 'app': " + trie.contains("app")); // true
        System.out.println("Contains 'appl': " + trie.contains("appl")); // false

        // Проверка префиксов
        System.out.println("StartsWith 'app': " + trie.startsWith("app")); // true
        System.out.println("StartsWith 'bat': " + trie.startsWith("bat")); // false

        // Получение слов по префиксу
        System.out.println("Words with prefix 'app': " + trie.getByPrefix("app"));
        System.out.println("Words with prefix 'ban': " + trie.getByPrefix("ban"));
        System.out.println("All words: " + trie.getByPrefix(""));

        // Удаление слова
        System.out.println("Remove 'app': " + trie.remove("app"));
        System.out.println("Contains 'app' after removal: " + trie.contains("app")); // false
        System.out.println("Words with prefix 'app' after removal: " + trie.getByPrefix("app"));

        // Размер дерева
        System.out.println("Trie size: " + trie.size());
        System.out.println("Is trie empty: " + trie.isEmpty());

        System.out.println(trie);
    }
}