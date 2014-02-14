/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package viewer.main.tree;

import data.mining.GSP;
import data.mining.InterestingPatterns;
import data.processing.RecoveryPattern;
import data.processing.SequenceStructure;
import java.awt.Graphics;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

/**
 *
 * @author Luciana
 */
public class Tree {
    private static DefaultMutableTreeNode root;
    private static JTree jtree;
    private static TreePath path;
    private static LinkedList<LinkedList<String>> sequentialPatternList;
    private DefaultTreeModel treemodel = new DefaultTreeModel(root);
    private static boolean isOk;


    public boolean getIsOk(){
        return isOk;
    }

    public DefaultMutableTreeNode getTree(){
        return root;
    }

    public static void setRoot(String root) {
        Tree.root = new DefaultMutableTreeNode(root);
    }

    public static void setSequentialPatternList(LinkedList<LinkedList<String>> sequentialPatternList) {
        Tree.sequentialPatternList = new LinkedList<LinkedList<String>>();
        Tree.sequentialPatternList.addAll(sequentialPatternList);
    }

    
    public JTree setTree(DefaultMutableTreeNode model){
        root = new DefaultMutableTreeNode();
        root = model;
        path = new TreePath(root);
        jtree = new JTree(root);
        TreeUtils.expandAll(jtree, path, true);
        return jtree;
    }

    public static JTree getJtree() {
        return jtree;
    }

    public JTree start(String strCurrentPath, int option, int size, double support, String subject, JPanel panel, JLabel label, boolean classOption) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException, IOException {
         isOk=false;
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//            JFrame frame = new JFrame();
//            Container contentPane = frame.getContentPane();
//            contentPane.setLayout(new GridLayout(1, 1));
            isOk = createTree(strCurrentPath, option, size, support, subject, panel, label, classOption);
//            if(isOk){
//            contentPane.add(new JScrollPane(jtree));
//            frame.setTitle(strCurrentPath + subject + " - Suporte: "+(support*100)+"%");
//            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//            frame.setSize(800, 800);
//            frame.setLocationRelativeTo(null);
//            frame.setVisible(true);
//            }

        return jtree;
    }

    
    private boolean createTree(String strCurrentPath, int option, int size, double support, String subject, JPanel panel, JLabel label, boolean classOption) throws IOException {
        root = new DefaultMutableTreeNode("root"+subject);
        LinkedList<LinkedList<String>> structure = seekSequentialPatterns(strCurrentPath, option, size, support, panel, label, classOption);
        label.setText("Construindo árvore das sequências recuperadas ... ");
        repaintComponent(panel);
        if(classOption){
            generateTree(structure);
            path = new TreePath(root);
            jtree = new JTree(root);
            TreeUtils.expandAllTree(jtree, path, true);
            return true;
        } else return buildTree(structure);      
    }

    public boolean buildTree(LinkedList<LinkedList<String>> structure){
        if(structure.size()>0 && sequentialPatternList.size()>0){
                InterestingPatterns biggest = new InterestingPatterns();
                biggest.setSequentialPatterns(structure);
                biggest.selectPatterns();
                LinkedList<LinkedList<String>> biggestPatterns = biggest.getSequentialPatterns();
                Iterator<LinkedList<String>> biggestIterator = biggestPatterns.descendingIterator();
                int counter = 1;
                while(biggestIterator.hasNext()){
                    LinkedList<String> chosenPatterns = biggestIterator.next();
                    Iterator<String> chosenIterator = chosenPatterns.iterator();
                    DefaultMutableTreeNode ancestor = new DefaultMutableTreeNode("Padrão Sequencial "+counter);
                    counter++;
                    while(chosenIterator.hasNext()){
                        DefaultMutableTreeNode node = new DefaultMutableTreeNode(chosenIterator.next());
                        ancestor.add(node);
                    }
                    addChildren(ancestor, chosenPatterns);
                    root.add(ancestor);
                }
                path = new TreePath(root);
                jtree = new JTree(root);
                TreeUtils.expandAll(jtree, path, true);
                return true;
            }
       return false;
    }

    private void addChildren(DefaultMutableTreeNode ancestor, LinkedList<String> structure){
        Iterator<LinkedList<String>> sequentialPatternIterator = sequentialPatternList.iterator();
        int sequence = 1;
        LinkedList<LinkedList<String>> indexes = new LinkedList<LinkedList<String>>();
        while(sequentialPatternIterator.hasNext()){
            LinkedList<String> sequentialPattern = sequentialPatternIterator.next();
            int mark = structure.indexOf(sequentialPattern.get(0).split("-")[1]);
            int index = 0;
            if(structure.size()>=sequentialPattern.size()){
              while(index < sequentialPattern.size() && mark != -1 && mark < structure.size()){
                 if(mark < structure.size() && index < sequentialPattern.size() && structure.get(mark).equals(sequentialPattern.get(index).split("-")[1])){
                    index++;
                    mark++;
                }else if(mark < structure.size()){/* Searchs the next occurrence of sequentialPattern.get(0) */
                    index=0;
                    mark = searchNextOccurrence(sequentialPattern.get(0).split("-")[1], structure, mark);
                }
              }
            if(index == sequentialPattern.size()){
                mark = mark - index;
                addNewChild(sequence, mark, sequentialPattern, ancestor);
                indexes.add(sequentialPattern);
                sequence++;
            }
            }
        }
        removePatterns(indexes);
    }
    
    private void removePatterns(LinkedList<LinkedList<String>> indexes){
        int index = -1;
        Iterator<LinkedList<String>> indexIterator = indexes.iterator();
        while(indexIterator.hasNext()){
            index = sequentialPatternList.indexOf(indexIterator.next());
            sequentialPatternList.remove(index);
        }
    }


    private void addNewChild(int sequence, int mark, LinkedList<String> sequentialPattern, DefaultMutableTreeNode ancestor) {
        int count = 0;
        int index = 0;
        Enumeration e = ancestor.children();
        while(e.hasMoreElements()){
             DefaultMutableTreeNode node = (DefaultMutableTreeNode)e.nextElement();
             if(count < mark) count++;
             else if(index < sequentialPattern.size()){
                 /*Funcionando 1a opção*/
                 boolean inserted = false;
                 if(!node.isLeaf()){
                     Enumeration enum1 = node.children();
                     while(enum1.hasMoreElements()){
                         DefaultMutableTreeNode child = (DefaultMutableTreeNode)enum1.nextElement();
                         String[] userObject = ((String)child.getUserObject()).split(" / ");
                         if(userObject[0].equals(sequentialPattern.get(index).split("-")[0])){
                                child.setUserObject(child.getUserObject()+"-"+sequence);
                                inserted = true;
                                break;
                         }
                     }
                 }
                 if(!inserted){
                    DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(sequentialPattern.get(index).split("-")[0]+" / "+sequence);
                    node.add(newChild);
                 }
                 /* primeira opção */
//                 DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(sequence + " - " +sequentialPattern.get(index).split("-")[0]);
//                 node.add(newChild);
                 index++;
             }else if(index == sequentialPattern.size()) break;
        }
    }



    private int searchNextOccurrence(String method, List<String> list, int index) {
        while(index < list.size()){
            if(list.get(index).equals(method)) return index;
            index++;
        }
        return -1;
    }

    private LinkedList<LinkedList<String>> seekSequentialPatterns(String strCurrentPath, int option, int size, double support, JPanel panel, JLabel label, boolean classOption) throws IOException {
        SequenceStructure sequences = new SequenceStructure(strCurrentPath);
        sequentialPatternList = new LinkedList<LinkedList<String>>();
        label.setText("Recuperando Sequências do Rastro");
        repaintComponent(panel);
        sequences.getTraceSequences();
        label.setText("Minerando sequências");
        repaintComponent(panel);
        GSP gsp = new GSP(support, size);
        RecoveryPattern recovery = new RecoveryPattern();
        gsp.getOption(sequences.getStructure(), option, classOption);
        if(option == 2) sequentialPatternList.addAll(recovery.getSkeleton(gsp.getPatterns(), sequences.getStructure()));
        else if(option == 3) sequentialPatternList.addAll(recovery.getSuffixSkeleton(gsp.getPatterns(), sequences.getStructure()));
        else if(option == 1) sequentialPatternList.addAll(recovery.getFullMethods(gsp.getPatterns(), sequences.getStructure()));
        else if((option == 4 || option ==5) && !classOption) {
            sequentialPatternList.addAll(gsp.getPatterns());
            return gsp.getRecoveredPatterns();
        }else {
            sequentialPatternList.addAll(recovery.getFullClasses(gsp.getPatterns(), sequences.getStructure()));
        }
        return recovery.getMethods();
    }

    private void repaintComponent(JPanel panel) {
        Graphics g = panel.getGraphics();
        panel.paint(g);
    }

    private void generateTree(LinkedList<LinkedList<String>> structure) {
        remove(structure);
        //remover repetições dentro de uma lista e se depois das remoções restar somente o valor q era repetido remover lista
        DefaultMutableTreeNode tree = new DefaultMutableTreeNode(structure.get(0).get(0));
        root.add(tree);
        Iterator<LinkedList<String>> sequenceIterator = structure.iterator();
        LinkedList<String> node = null;
        while(sequenceIterator.hasNext()){
            LinkedList<String> sequence = sequenceIterator.next();
            boolean inserted = true;
            for(int i = 0; i < root.getChildCount(); i++) {
                tree = (DefaultMutableTreeNode)root.getChildAt(i);
                if(tree.getUserObject().toString().equals(sequence.get(0))){
                    node = new LinkedList<String>();
                    node.addAll(sequence.subList(1, sequence.size()));
                    node = addChildrenSub(tree, node);
                    if(node.size() == 0) {
                        inserted = true;
                        break;
                    }
                }else if(!tree.isLeaf()) {
                    inserted = lookForIt(tree, sequence);
                    if(inserted) break;
                }
            }
            if(!inserted) {
                tree = new DefaultMutableTreeNode(sequence.get(0));
                node = new LinkedList<String>();
                node.addAll(sequence.subList(1, sequence.size()));
                addChildrenSub(tree, node);
                root.add(tree);
            }
        }
        root.add(tree);
    }

    private LinkedList<String> addChildrenSub(DefaultMutableTreeNode tree, LinkedList<String> subList) {
        if(subList.size() > 0) {
            if(!lookForIt(tree, subList)) {
                DefaultMutableTreeNode child = new DefaultMutableTreeNode(subList.get(0));
                tree.add(child);
                LinkedList<String> node = new LinkedList<String>();
                node.addAll(subList.subList(1, subList.size()));
                return addChildrenSub(child, node);
            } else subList.clear();
        }
        return subList;
    }

    private boolean lookForIt(DefaultMutableTreeNode tree, LinkedList<String> sequence) {
        for(int i = 0; i < tree.getChildCount(); i++) {
           DefaultMutableTreeNode child = (DefaultMutableTreeNode)tree.getChildAt(i);
           if(child.getUserObject().toString().equals(sequence.get(0))){
               LinkedList<String> node = new LinkedList<String>();
               node.addAll(sequence.subList(1, sequence.size()));
               addChildrenSub(child, node);
               return true;
           }else if(!child.isLeaf()) if(lookForIt(child, sequence)) return true;
        }
        return false;
    }

    /**
     * Remove repetições dentro de uma lista
     * @param structure
     */
    private void remove(LinkedList<LinkedList<String>> structure) {
        Iterator<LinkedList<String>> navigate = structure.iterator();
        while(navigate.hasNext()){
            LinkedList<String> list = navigate.next();
            for(int i = 0; i < list.size(); i++){
                for(int j = i + 1; j < list.size(); j++){
                    if(list.get(i).equals(list.get(j))){
                        list.remove(j);
                        j = i;
                    }
                }
            }
//            if(list.size() == 1) structure.removeAll(list);
        }
    }
}
