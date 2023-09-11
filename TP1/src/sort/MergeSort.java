package sort;

import java.util.ArrayList;

import model.PlayerRegister;

public class MergeSort {
  public static void mergeSort(ArrayList<PlayerRegister> lista) {
    mergeSort(lista, 0, lista.size() - 1);
  }

  private static void mergeSort(ArrayList<PlayerRegister> arr, int left, int right) {
    if (left < right) {
      int mid = left + (right - left) / 2;
      mergeSort(arr, left, mid);
      mergeSort(arr, mid + 1, right);
      merge(arr, left, mid, right);
    }
  }

  private static void merge(ArrayList<PlayerRegister> arr, int left, int mid, int right) {
    int n1 = mid - left + 1;
    int n2 = right - mid;

    ArrayList<PlayerRegister> leftArr = new ArrayList<>();
    ArrayList<PlayerRegister> rightArr = new ArrayList<>();

    for (int i = 0; i < n1; i++) {
      leftArr.add(arr.get(left + i));
    }
    for (int j = 0; j < n2; j++) {
      rightArr.add(arr.get(mid + 1 + j));
    }

    int i = 0, j = 0, k = left;

    while (i < n1 && j < n2) {
      if (leftArr.get(i).getPlayer().getPlayerId() <= rightArr.get(j).getPlayer().getPlayerId()) {
        arr.set(k, leftArr.get(i));
        i++;
      } else {
        arr.set(k, rightArr.get(j));
        j++;
      }
      k++;
    }

    while (i < n1) {
      arr.set(k, leftArr.get(i));
      i++;
      k++;
    }

    while (j < n2) {
      arr.set(k, rightArr.get(j));
      j++;
      k++;
    }
  }
}
