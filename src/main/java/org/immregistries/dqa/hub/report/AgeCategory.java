package org.immregistries.dqa.hub.report;

public enum AgeCategory {
    /* These age ranges are inclusive of the lower bound, and exclusive of upper bound*/
      BABY (0,1)
    , CHILD (1,7)
    , TEENAGER (7,18)
    , ADULT (18,55)
    , SENIOR (55,200)
    , OTHER(-1,-1);

    public int ageLow;
    public int ageHigh;

    AgeCategory(int low, int high) {
        this.ageHigh = high;
        this.ageLow = low;
    }

    public static AgeCategory getCategoryForAge(int age) {
          for (AgeCategory ac : AgeCategory.values()) {
              if (ac.isInCategory(age)) {
                  return ac;
              }
          }

          return OTHER;
    }

    public boolean isInCategory(int age) {
        return age >= ageLow && age < ageHigh;
    }
}
