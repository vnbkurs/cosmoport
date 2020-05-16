package com.space.controller;


import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@ResponseBody
@RequestMapping(value = "/rest")
public class ShipController {

    private final ShipService shipService;

    @Autowired
    public ShipController(ShipService shipService) {
        this.shipService = shipService;
    }

    @RequestMapping(value = "/ships",method = RequestMethod.GET)
    public ResponseEntity<List<Ship>> getShipList( String name,String planet,ShipType shipType,Long after,Long before,
                                   Boolean isUsed,Double minSpeed,Double maxSpeed,Integer minCrewSize,
                                   Integer maxCrewSize,Double minRating,Double maxRating,
                                  @RequestParam(required = false) ShipOrder order,
                                  @RequestParam(required = false) Integer pageNumber,
                                  @RequestParam(required = false) Integer pageSize) {
        List<Ship> filteredShips = shipService.getShipList(name, planet, shipType, after, before, isUsed,
                                                           minSpeed,maxSpeed, minCrewSize, maxCrewSize,
                                                           minRating, maxRating);
        return new ResponseEntity<>(shipService.filteredShips(filteredShips, order, pageNumber, pageSize),HttpStatus.OK);
     }

    @RequestMapping(value = "/ships/{id}",method = RequestMethod.GET)
    public ResponseEntity<Ship> getShipById(@PathVariable Long id) {
         if(id == null && id < 0 &&  id - Math.floor(id) !=0){
             return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

         } else

         return new ResponseEntity<>(shipService.getShipById(id),HttpStatus.OK);
    }

    @RequestMapping(value = "/ships",method = RequestMethod.POST)
    public ResponseEntity<Ship> createShip(@RequestBody Ship ship) {
        Ship createShip = shipService.createShip(ship);
        if (createShip == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(createShip,HttpStatus.OK);
    }

    @RequestMapping(value = "/ships/{id}",method = RequestMethod.DELETE)
    public ResponseEntity<Ship> deleteShip(@PathVariable Long id) {
        if(id == null && id < 0 && id - Math.floor(id) !=0){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else

        shipService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/ships/{id}",method = RequestMethod.POST)
    public ResponseEntity<Ship> updateShip(@RequestBody Ship ship, @PathVariable Long id) {
        if(id != null && id > 0 &&  id - Math.floor(id) !=0 ){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }else

        return new ResponseEntity<>(shipService.updateShip(ship, id),HttpStatus.OK);
    }

    @RequestMapping(value = "/ships/count",method = RequestMethod.GET)
    public ResponseEntity<Integer>  getShipCount(String name,String planet,ShipType shipType,Long after, Long before,
                            Boolean isUsed,Double minSpeed,Double maxSpeed,Integer minCrewSize,
                            Integer maxCrewSize, Double minRating, Double maxRating) {
        int count = shipService.getShipList(name, planet, shipType, after, before, isUsed, minSpeed,
                maxSpeed, minCrewSize,maxCrewSize, minRating, maxRating).size();
        return new ResponseEntity<>(count,HttpStatus.OK);
    }

}