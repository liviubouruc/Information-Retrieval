import java.io.IOException;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Path to the documents you want to index: ");
        String documentsPath = scanner.nextLine();
        System.out.print("Path to where you want your documents to be indexed: ");
        String indexPath = scanner.nextLine();

//        String documentsPath = "/Users/petru-liviubouruc/Documents/Master/An2Sem1/IR&TM/P1/InformationRetrieval/documents";
//        String indexPath = "/Users/petru-liviubouruc/Documents/Master/An2Sem1/IR&TM/P1/InformationRetrieval/indexed";

        try(Indexer indexer = new Indexer(indexPath)) {
            indexer.indexDocuments(documentsPath);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        System.out.print("Enter word for search query: ");
        String query = scanner.nextLine();
        try (Retriever searcher = new Retriever(indexPath)) {
            searcher.search(query);
        } catch (Exception e) {
            System.out.println("Searching failed: " + e.getMessage());
        }
    }
}
