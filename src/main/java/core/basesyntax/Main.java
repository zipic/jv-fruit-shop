package core.basesyntax;

import dao.StorageDao;
import dao.StorageDaoImpl;
import handler.OperationHandler;
import handler.OperationHandlerBalance;
import handler.OperationHandlerPurchase;
import handler.OperationHandlerReturn;
import handler.OperationHandlerSupply;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.FruitTransaction;
import service.FileReader;
import service.FileWriter;
import service.ParseService;
import service.ReportCreator;
import service.TransactionProcessor;
import serviceimpl.FileWriterImpl;
import serviceimpl.ParseServiceImpl;
import serviceimpl.ReadFileImpl;
import serviceimpl.ReportCreatorImpl;
import serviceimpl.TransactionImpl;
import strategy.OperationStrategy;
import strategy.OperationStrategyImpl;

public class Main {
    private static final String WAY_TO_INPUT = "src/main/resources/StartDay.csv";
    private static final String WAY_TO_REPORT = "src/main/resources/CloseDay.csv";

    public static void main(String[] args) {
        StorageDao storageDao = new StorageDaoImpl();
        Map<FruitTransaction.Operation, OperationHandler> operationMap = new HashMap<>();
        operationMap.put(FruitTransaction.Operation.BALANCE,
                new OperationHandlerBalance(storageDao));
        operationMap.put(FruitTransaction.Operation.SUPPLY,
                new OperationHandlerSupply(storageDao));
        operationMap.put(FruitTransaction.Operation.PURCHASE,
                new OperationHandlerPurchase(storageDao));
        operationMap.put(FruitTransaction.Operation.RETURN,
                new OperationHandlerReturn(storageDao));
        FileReader readFile = new ReadFileImpl();
        List<String> data = readFile.readFromFile(WAY_TO_INPUT);
        ParseService parseService = new ParseServiceImpl();
        List<FruitTransaction> transactions = parseService.parseService(data);
        OperationStrategy operationStrategy = new OperationStrategyImpl(operationMap);
        TransactionProcessor transaction = new TransactionImpl(operationStrategy);
        transaction.process(transactions);
        ReportCreator editor = new ReportCreatorImpl(storageDao);
        String report = editor.create();
        FileWriter writeToFile = new FileWriterImpl();
        writeToFile.writeToFile(WAY_TO_REPORT, report);
    }
}