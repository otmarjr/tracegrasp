/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package builder.tree;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Luciana
 */
public class Node {
  private String label;
  private String packageName;
  private int level;
  private int levelCompressed;
  private int subNodeNumber;
  private int idNode;
  private int idFather;
  private boolean belongsToCluster;
  private LinkedList<Node> children;
  private LinkedList<String> parameterValues;


  public Node(){
    label = "";
    packageName = "";
    level = -1;
    levelCompressed = -1;
    subNodeNumber = 0;
    idNode = -1;
    belongsToCluster = false;
    idFather = -1;
    children = new LinkedList<Node>();
    parameterValues = new LinkedList<String>();
  }

    
  /** 
   * Treats labels
   */


  public void setLabel(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }

  public String getPackageName(){
      return packageName;
  }

  public void setPackageName(String packageName){
      this.packageName = packageName;
  }

  public void setLevel(int level){
      this.level = level;
  }

  public void setLevelCompressed(int level){
      this.levelCompressed = level;
  }

  public void setSubNodeNumber(int subNodes){
      this.subNodeNumber = subNodes;
  }

  public int getLevel(){
      return level;
  }

  public int getLevelCompressed(){
      return levelCompressed;
  }

    public boolean isBelongsToCluster() {
        return belongsToCluster;
    }

    public void setBelongsToCluster(boolean belongsToCluster) {
        this.belongsToCluster = belongsToCluster;
    }

    public int getIdNode() {
        return idNode;
    }

    public void setIdNode(int idNode) {
        this.idNode = idNode;
    }

    public int getIdFather() {
        return idFather;
    }

    public void setIdFather(int idFather) {
        this.idFather = idFather;
    }



  /** 
   * Treats children
   */

  public void increaseSubNodes(){
      this.subNodeNumber++;
  }

  public int getSubNodeNumber(){
      return this.subNodeNumber;
  }

  public void addChild(Node child) {
    this.children.add(child);
  }

  public void addChild(Node child, int index) {
    this.children.set(index, child);
  }

  public void addChildren(List<Node> children){
      this.children.addAll(children);
  }

  public boolean hasChildren() {
    try {
    if (!this.children.isEmpty())
      return true;
    else
      return false;
    } catch (Exception e) {
      return false;
    }
  }
//  public void addPath(LinkedList<Node> path) {
//    this.path.addAll(path);
//  }
//
//  public LinkedList<Node> getPath() {
//    return this.path;
//  }

  public LinkedList<Node> getChildren() {
    return this.children;
  }

  public int getChildCount() {
      return this.children.size();
  }

  public Node getChildAt(int index){
      return this.children.get(index);
  }

  /** 
   * Treats path
   */
//
//  public void addPath(LinkedList<Node> path) {
//    this.path.addAll(path);
//  }
//
//  public LinkedList<Node> getPath() {
//    return this.path;
//  }

  public void setParameterValues(String parameter){
      parameterValues.add(parameter);
  }

  public LinkedList<String> getParameterValues(){
      return parameterValues;
  }

}
