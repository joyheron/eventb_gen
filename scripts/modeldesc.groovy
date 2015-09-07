import de.prob.model.representation.ElementComment
import de.prob2.gen.ModelGenerator

c = """
context CLift

constants groundf topf 

axioms
  @axm5 topf = 1
  @axm1 groundf ∈ ℤ
  @axm2 groundf = −1
  @axm3 groundf < topf
  @axm4 topf ∈ ℤ
end
"""

m = """
machine MLift // so much to comment on!
 sees CLift 

variables cur_floor inside_buttons door_open call_buttons direction_up 

invariants
  @inv1 cur_floor ∈ (groundf ‥ topf)
  @inv2 inside_buttons ⊆ (groundf ‥ topf)
  @inv3 door_open ∈ BOOL
  @inv4 call_buttons ⊆ (groundf ‥ topf)
  @inv5 direction_up ∈ BOOL

events
  event INITIALISATION
    then
      @act1 cur_floor ≔ groundf
      @act2 inside_buttons ≔ ∅
      @act3 door_open ≔ FALSE
      @act4 call_buttons ≔ ∅
      @act5 direction_up ≔ TRUE
  end

  event move_up
    where
      @grd1 door_open = FALSE
      @grd2 cur_floor < topf
      @grd3 direction_up = TRUE
    then
      @act1 cur_floor ≔ cur_floor + 1
  end

  event move_down
    where
      @grd1 door_open = FALSE
      @grd2 cur_floor > groundf
      @grd3 direction_up = FALSE
    then
      @act1 cur_floor ≔ cur_floor − 1
  end

  event reverse_lift_down
    where
      @grd1 direction_up = TRUE
    then
      @act1 direction_up ≔ FALSE
  end

  event reverse_lift_up
    where
      @grd1 direction_up = FALSE
    then
      @act1 direction_up ≔ TRUE
  end

  event open_door
    where
      @grd1 door_open = FALSE
      @grd2 cur_floor ∈ (inside_buttons ∪ call_buttons)
    then
      @act1 door_open ≔ TRUE
  end

  event close_door
    where
      @grd1 door_open = TRUE
    then
      @act1 door_open ≔ FALSE
      @act2 inside_buttons ≔ inside_buttons ∖ {cur_floor}
      @act3 call_buttons ≔ call_buttons ∖ {cur_floor}
  end

  event push_inside_button
    any b 
    where
      @grd1 b ∈ (groundf ‥ topf)
      @grd2 b ∉ inside_buttons
      @grd3 b ≠ cur_floor
    then
      @act1 inside_buttons ≔ inside_buttons ∪ {b}
  end

  event push_call_button
    any b 
    where
      @grd1 b ∈ (groundf ‥ topf)
      @grd2 b ∉ call_buttons
    then
      @act1 call_buttons ≔ call_buttons ∪ {b}
  end
end
"""

ModelGenerator mg = new ModelGenerator();
mg.addComponent(c);
mg.addComponent(m);

println mg.getModel().MLift.getChildrenOfType(ElementComment.class).collect { it.getComment() }

mg.getModel()