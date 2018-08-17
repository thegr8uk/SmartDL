package com.itsmeyuke.smartdl.model;
import java.util.Arrays;

public class CyclicArray<T> {

	transient Object[] elementData;
	transient int capacity;
	transient int currentSize;
	
	public CyclicArray(int capacity) {
		this.capacity = capacity;
		this.elementData = new Object[capacity];
	}
	
	public boolean add(T o) {
		if(this.capacity == this.currentSize) {
			Object[] newElementData = new Object[this.capacity];
			for (int i = 0; i < this.elementData.length-1; i++) {
				newElementData[i] = this.elementData[i+1];
			}
			this.elementData = Arrays.copyOf(newElementData, this.capacity);
			this.elementData[this.capacity - 1] = o;
			this.currentSize = this.capacity;
			return true;
		}
		this.elementData[this.currentSize] = o;
		this.currentSize = this.currentSize+1;
		return true;
	}
	
	public double average() {
		
		if(this.currentSize == 0)
			return 0;
		
		if(! (this.elementData[0] instanceof Double))
			return 0;
		
		double thisData = 0;
		double sum = 0;
		for (int i = 0; i < elementData.length; i++) {
			Double object = (Double) elementData[i];
			thisData = object == null ? thisData : object;
			sum += thisData;
		}
		return (sum/elementData.length);
	}
	
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("[");
		for (int i = 0; i < this.elementData.length; i++) {
			s.append(this.elementData[i]);
			if(i != this.elementData.length-1)
				s.append(",");
		}
		s.append("]");
		return s.toString();
	}
}
