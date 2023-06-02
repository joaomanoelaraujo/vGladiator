package br.com.johnmanoel.d4rkk.glad.cosmetics;


public enum CosmeticType {

  WIN_ANIMATION("Comemorações de Vitória");

  
  private final String[] names;
  
  CosmeticType(String... names) {
    this.names = names;
  }
  
  public String getName(long index) {
    return this.names[(int) (index - 1)];
  }
}
