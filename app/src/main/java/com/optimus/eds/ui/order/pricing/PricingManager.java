package com.optimus.eds.ui.order.pricing;

import android.app.Application;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.optimus.eds.Enums;
import com.optimus.eds.db.AppDatabase;
import com.optimus.eds.db.dao.PriceConditionEntitiesDao;
import com.optimus.eds.db.dao.PricingDao;
import com.optimus.eds.db.dao.ProductsDao;
import com.optimus.eds.db.entities.CartonPriceBreakDown;
import com.optimus.eds.db.entities.OrderDetail;
import com.optimus.eds.db.entities.OutletAvailedPromotion;
import com.optimus.eds.db.entities.Product;
import com.optimus.eds.db.entities.UnitPriceBreakDown;
import com.optimus.eds.db.entities.pricing.FreeGoodDetails;
import com.optimus.eds.db.entities.pricing.FreeGoodGroups;
import com.optimus.eds.db.entities.pricing.OutletAvailedFreeGoods;
import com.optimus.eds.db.entities.pricing.PriceAccessSequence;
import com.optimus.eds.db.entities.pricing.PriceConditionClass;
import com.optimus.eds.db.entities.pricing.PriceConditionDetail;
import com.optimus.eds.db.entities.pricing.PriceConditionEntities;
import com.optimus.eds.db.entities.pricing.PriceConditionScale;
import com.optimus.eds.db.entities.pricing.PriceConditionType;
import com.optimus.eds.db.entities.pricing.PricingArea;
import com.optimus.eds.db.entities.pricing_models.PriceConditionDetailsWithScale;
import com.optimus.eds.model.OrderResponseModel;
import com.optimus.eds.model.UdtProductQuantity;
import com.optimus.eds.ui.order.free_goods.FreeGoodOutputDTO;
import com.optimus.eds.ui.order.free_goods.prGetFreeGoods;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

import static android.graphics.BlurMaskFilter.Blur.OUTER;

public class PricingManager {
    private String TAG = PricingManager.class.getSimpleName();
    private static PricingManager ourInstance = null;

    private final AppDatabase appDatabase;
    private final PricingDao pricingDao;
    private final ProductsDao productsDao;
    private final PriceConditionEntitiesDao priceConditionEntitiesDao;

    public static PricingManager getInstance(Application application) {
        if (ourInstance == null)
            ourInstance = new PricingManager(application);
        return ourInstance;
    }

    private PricingManager(Application application) {

        appDatabase = AppDatabase.getDatabase(application);
        pricingDao = appDatabase.pricingDao();
        productsDao = appDatabase.productsDao();
        priceConditionEntitiesDao = appDatabase.priceConditionEntitiesDao();
    }


    public Integer priceConditionClassValidation(){
        return pricingDao.priceConditionClassValidation().subscribeOn(Schedulers.io()).blockingGet();
    }

    public Integer priceConditionValidation(){
        return pricingDao.priceConditionValidation().subscribeOn(Schedulers.io()).blockingGet();
    }

    public Integer priceConditionTypeValidation(){
        return pricingDao.priceConditionTypeValidation().subscribeOn(Schedulers.io()).blockingGet();
    }


    //region "Public Functions"

    public PriceOutputDTO getOrderPrice(OrderResponseModel orderResponseModel , BigDecimal orderTotalAmount, int quantity, int outletId, int routeId, Integer distributionId , String date) {
        PriceOutputDTO objPriceOutputDTO = new PriceOutputDTO();

        // AccessSequenceDTO appliedAccessSequence = new AccessSequenceDTO();
        //decimal totalPrice = 0; //input price for every condition class
        totalPrice = orderTotalAmount; //input price for every condition class
        isPriceFound = false;
        // @TODO  Remove pricingArea Loop if data comes from server with filtered pricing Areas
        List<PricingArea> pricingAreas = pricingDao.findPricingArea().subscribeOn(Schedulers.io()).blockingGet();
        List<PriceConditionClass> conditionClasses = pricingDao.findPriceConditionClasses(2).subscribeOn(Schedulers.io()).blockingGet();
        for (PriceConditionClass conditionClass : conditionClasses) {
            isPriceFound = false;
            List<PriceConditionType> conditionTypes = pricingDao.findPriceConditionTypes(conditionClass.getPriceConditionClassId()).subscribeOn(Schedulers.io()).blockingGet();
            for (PriceConditionType conditionType : conditionTypes) {

                List<PriceConditionWithAccessSequence> priceConditions = pricingDao
                        .getPriceConditionAndAccessSequenceByTypeId(conditionType.getPriceConditionTypeId() , orderResponseModel.getOutlet().getVpoClassificationId() , orderResponseModel.getOutlet().getPricingGroupId()
                                , orderResponseModel.getChannelId() , orderResponseModel.getOrganizationId() , orderResponseModel.getOutlet().getPromoTypeId(),
                                orderResponseModel.getOutlet().getCustomerRegistrationTypeId() , date , distributionId , outletId).subscribeOn(Schedulers.io()).blockingGet();


                Collections.sort(priceConditions, (o1, o2) -> o1.getOrder().compareTo(o2.getOrder()));
                // for distribution check
//                    if (conditionClass.PricingType == (int)EnumPricingType.DistributionPricing)
//                    {
//                        priceConditions = priceConditions.Where(x => x.DistributionId == distributionId).ToList();
//                    }

                for (PriceConditionWithAccessSequence priceCondition : priceConditions) {

                    PromoLimitDTO limit = GetPriceAgainstPriceConditionForInvoice(priceCondition.getPriceConditionId(), priceCondition.getSequenceCode(),
                            outletId, quantity, totalPrice, conditionType.getPriceScaleBasisId(), routeId, distributionId , orderResponseModel.getOutlet().getChannelId());

                    if (limit != null && limit.getUnitPrice().doubleValue() > -1) {
                        isPriceFound = true;

                        //Block output in price
                        UnitPriceBreakDown objSingleBlock = new UnitPriceBreakDown();

                        if (limit.getLimitBy() != null) {
                            objSingleBlock.setMaximumLimit(limit.getMaximumLimit().doubleValue());
                            objSingleBlock.setLimitBy(limit.getLimitBy());

                            // added By Husnain

                            if (objSingleBlock.getLimitBy() == (int)Enums.LimitBy.Amount && priceCondition.getCombinedMaxValueLimit() != null)
                            {
                                objSingleBlock.setMaximumLimit(priceCondition.getCombinedMaxValueLimit());
                            }
                            else if (objSingleBlock.getLimitBy() == (int)Enums.LimitBy.Quantity && priceCondition.getCombinedMaxCaseLimit() != null)
                            {
                                objSingleBlock.setMaximumLimit(priceCondition.getCombinedMaxCaseLimit());
                            }

                      /*      var alreadyAvailed = _outletAvailedPromotionDataHandler.GetAlreadyAvailedValue(objSingleBlock.PriceConditionDetailId);
                            if (alreadyAvailed != null)
                            {
                                if (objSingleBlock.LimitBy == (int)Enums.LimitBy.Amount)
                                {
                                    objSingleBlock.AlreadyAvailed = alreadyAvailed.Amount;
                                }
                                else
                                {
                                    objSingleBlock.AlreadyAvailed = alreadyAvailed.Quantity;
                                }
                            }*/
                        }


//                            CalculateBlockPrice(totalPrice, limit.getUnitPrice(), quantity, conditionType.getPriceScaleBasisId(), conditionType.getOperationType(), conditionType.getCalculationType(), conditionType.getRoundingRule(), objSingleBlock.getLimitBy(), objSingleBlock.getMaximumLimit(), objSingleBlock.getAlreadyAvailed(), totalPrice);

                        ItemAmountDTO blockPrice = CalculateBlockPrice(totalPrice.doubleValue(), limit.getUnitPrice().doubleValue(), quantity, conditionType.getPriceScaleBasisId(), conditionType.getOperationType(), conditionType.getCalculationType(), conditionType.getRoundingRule(), objSingleBlock.getLimitBy(), objSingleBlock.getMaximumLimit(), objSingleBlock.getAlreadyAvailed() , totalPrice.toBigInteger().doubleValue());

                        totalPrice = BigDecimal.valueOf(blockPrice.getTotalPrice());
                        objSingleBlock.setPriceConditionType(conditionType.getName());
                        objSingleBlock.setPriceConditionClass(conditionClass.getName());
                        objSingleBlock.setPriceCondition(priceCondition.getName());
                        objSingleBlock.setAccessSequence(priceCondition.getSequenceName());
                        objSingleBlock.setCalculationType(conditionType.getCalculationType());
                        objSingleBlock.setUnitPrice(limit.getUnitPrice().floatValue());
                        objSingleBlock.setBlockPrice(blockPrice.getBlockPrice());
                        objSingleBlock.setPriceConditionDetailId(limit.getPriceConditionDetailId());
                        objSingleBlock.setPriceConditionId(priceCondition.getPriceConditionId());
                        objSingleBlock.setmPriceConditionClassId(conditionClass.getPriceConditionClassId());

                        objSingleBlock.setTotalPrice(totalPrice.doubleValue());
                        objPriceOutputDTO.getPriceBreakdown().add(objSingleBlock);
                        if (blockPrice.isMaxLimitReached()) {
                            Message message = new Message();

                            message.setMessageSeverityLevel(conditionClass.getSeverityLevel());
                            message.setMessageText("Max limit crossed for " + objSingleBlock.getPriceCondition());

                            objPriceOutputDTO.getMessages().add(message);
                        }
                        break;
                    }
                }
            }
            if (!isPriceFound && !TextUtils.isEmpty(conditionClass.getSeverityLevelMessage())
                    && conditionClass.getSeverityLevel() != Enums.MessageSeverityLevel.MESSAGE) {
                Message message = new Message();
                message.setMessageSeverityLevel(conditionClass.getSeverityLevel());
                message.MessageText = conditionClass.getSeverityLevelMessage();
                objPriceOutputDTO.getMessages().add(message);
            }
        }
        objPriceOutputDTO.setTotalPrice(BigDecimal.valueOf(Math.round(totalPrice.doubleValue())));

//        // Distribution Pricing
//        isPriceFound = false;
//
//        var conditionClassesDist = _conditionClassRepository.Find(x => x.IsActive == true
//                &&
//                x.PricingLevelId == (int)EnumPriceTypeLevel.Product
//                &&
//                x.PricingType == (int)EnumPricingType.DistributionPricing
//                &&
//                (x.IsPeriodic == isPeriodic)).OrderBy(x => x.Order).ToList();
//
//        ApplyConditionClassesItemLevel(objPriceOutputDTO, conditionClassesDist, isPriceFound, ref totalPrice, productListDTOWithPackageIds, outletId, productDefinitionId, quantity, routeId, channelId, distributionId, orderDate, combinedMaxLimitHolderDTO, isPeriodic, organizationId);
//
//
//// Distribution Pricing
//
        return objPriceOutputDTO;
    }


    public Single<OrderResponseModel> calculatePriceBreakdown(OrderResponseModel orderModel , String date) {

        return Single.create(emitter -> {
            Gson gson = new Gson();


            HashMap<OrderDetail, List<OrderDetail>> orderItems = ComposeNewOrderItemsListForCalc(orderModel.getOrderDetails());

            List<OrderDetail> finalOrderDetailList = new ArrayList<>();
            Double payable = 0.0;
            // Added by Husnain
            Double totalQuantity = 0.0;
            PriceOutputDTO priceOutputDTO = null;
            List<ProductQuantity> productQuantityDTOList = new ArrayList<>();
            for (Map.Entry<OrderDetail, List<OrderDetail>> orderDetailHashMap : orderItems.entrySet()) {
                for (OrderDetail orderDetail : orderDetailHashMap.getValue()) {
                    // Added Check by Husnain
                    if (!orderDetail.type.equals("freegood")){
                        productQuantityDTOList.add(new ProductQuantity(orderDetail.getProductTempDefId(), orderDetail.getProductTempQuantity() , orderDetail.getPkgId()));
                        totalQuantity = totalQuantity + orderDetail.getProductTempQuantity();
                    }
                }
            }

            Completable.fromAction(pricingDao::deleteAllTempQty)
                    .andThen(addProductQty(productQuantityDTOList))
                    .subscribeOn(Schedulers.io()).blockingAwait();


            List<CombinedMaxLimitHolderDTO> combinedMaxLimitHolderDTOList = new ArrayList<>();
            for (Map.Entry<OrderDetail, List<OrderDetail>> orderDetailHashMap : orderItems.entrySet()) {
                OrderDetail orderDetailKey = orderDetailHashMap.getKey();
                for (OrderDetail orderDetail : orderDetailHashMap.getValue()) {


                    if (!orderDetail.type.equals("freegood")){
                        priceOutputDTO = getOrderItemPrice(productQuantityDTOList , orderDetail.getMobileOrderDetailId(), orderModel.getOutletId()
                                , orderDetail.getProductTempDefId(), orderDetail.getProductTempQuantity()
                                , orderModel.getRouteId(), orderModel.getDistributionId() , combinedMaxLimitHolderDTOList , orderModel , date , orderDetail.getProductId() ); // Missing organizationID , isPeriodic // orderDate // Add ProductListDTO

                        String gsonText = gson.toJson(priceOutputDTO.getPriceBreakdown());
                        if (orderDetail.getProductTempDefId() == orderDetail.getCartonDefinitionId()) {
                            List<CartonPriceBreakDown> priceBreakDown = gson.fromJson(gsonText, new TypeToken<List<CartonPriceBreakDown>>() {
                            }.getType());
                            orderDetailKey.setCartonPriceBreakDown(priceBreakDown);
                            orderDetailKey.setCartonQuantity(orderDetail.getProductTempQuantity());
                            orderDetailKey.setCartonTotalPrice(priceOutputDTO.getTotalPrice().doubleValue());

                        } else {
                            orderDetailKey.setUnitPriceBreakDown(new ArrayList<>(priceOutputDTO.getPriceBreakdown()));
                            orderDetailKey.setUnitQuantity(orderDetail.getProductTempQuantity());
                            orderDetailKey.setUnitTotalPrice(priceOutputDTO.getTotalPrice().doubleValue());
                        }
                        orderDetailKey.setCartonOrderDetailId(orderDetail.getCartonOrderDetailId());
                        orderDetailKey.setUnitOrderDetailId(orderDetail.getUnitOrderDetailId());
                        orderDetailKey.setProductTempDefId(orderDetail.getProductTempDefId());
                        orderDetailKey.setProductTempQuantity(orderDetail.getProductTempQuantity());
                        orderDetailKey.setLocalOrderId(orderDetail.getLocalOrderId());

                        payable += priceOutputDTO.getTotalPrice().doubleValue();
                    }
                }

                finalOrderDetailList.add(orderDetailKey);

            }
            orderModel.setPayable(payable);//DecimalFormatter.round(payable,0));
            orderModel.setSubtotal(payable);//DecimalFormatter.round(payable,2));
            orderModel.setOrderDetails(finalOrderDetailList);
            orderModel.setSuccess(true);
            emitter.onSuccess(orderModel);

        });


    }

    public HashMap<OrderDetail, List<OrderDetail>> ComposeNewOrderItemsListForCalc(List<OrderDetail> originalDetailsList) {
        HashMap<OrderDetail, List<OrderDetail>> orderDetailListHashMap = new HashMap<>();

        for (OrderDetail orderDetail : originalDetailsList) {
            List<OrderDetail> newOrderLineItems = new ArrayList<>();
            try {
                if (orderDetail.getUnitQuantity() != null && orderDetail.getUnitQuantity() > 0) {
                    OrderDetail newUnitProd = (OrderDetail) orderDetail.clone();
                    newUnitProd.setProductTempDefId(orderDetail.getUnitDefinitionId());
                    newUnitProd.setProductTempQuantity(orderDetail.getUnitQuantity());
                    newOrderLineItems.add(newUnitProd);
                }
                if (orderDetail.getCartonQuantity() != null && orderDetail.getCartonQuantity() > 0) {
                    OrderDetail newCartonProd = (OrderDetail) orderDetail.clone();
                    newCartonProd.setProductTempDefId(orderDetail.getCartonDefinitionId());
                    newCartonProd.setProductTempQuantity(orderDetail.getCartonQuantity());
                    newOrderLineItems.add(newCartonProd);
                }
            } catch (CloneNotSupportedException e) {
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
                return orderDetailListHashMap;
            }
            orderDetailListHashMap.put(orderDetail, newOrderLineItems);
        }
        return orderDetailListHashMap;


    }

    // region "Access Sequence Amount"
    private PromoLimitDTO GetPriceAgainstPriceConditionForInvoice(int priceConditionId, String accessSequence, int outletId, int quantity, BigDecimal totalPrice, int scaleBasisId, int routeId, Integer distributionId , Integer channelId) {
        PromoLimitDTO promoLimitDTO = new PromoLimitDTO();
        if (accessSequence.equalsIgnoreCase(Enums.AccessSequenceCode.OUTLET.toString())) {
            promoLimitDTO = this.GetPriceAgainstOutlet(priceConditionId, outletId, quantity, totalPrice, scaleBasisId);
        } else if (accessSequence.equalsIgnoreCase(Enums.AccessSequenceCode.ROUTE.toString())) {
            promoLimitDTO = this.GetPriceAgainstRoute(priceConditionId, routeId, quantity, totalPrice, scaleBasisId);
        } else if (accessSequence.equalsIgnoreCase(Enums.AccessSequenceCode.DISTRIBUTION.toString())) {
            promoLimitDTO = this.GetPriceAgainstDistribution(priceConditionId, distributionId, quantity, totalPrice, scaleBasisId);
        }
        return promoLimitDTO;
    }

    private PromoLimitDTO GetPriceAgainstPriceCondition(int priceConditionId, String accessSequence, int outletId, int productDefinitionId, int quantity, BigDecimal totalPrice,
                                                        int scaleBasisId, int routeId, Integer distributionId, Integer bundleId , Integer channelId , Long packageId , Integer pcDefinitionLevelId ) {
        PromoLimitDTO promoLimitDTO = new PromoLimitDTO();
        if (accessSequence.equalsIgnoreCase(Enums.AccessSequenceCode.OUTLET_PRODUCT.toString())) {
            promoLimitDTO = GetPriceAgainstOutletProduct(priceConditionId, outletId, productDefinitionId, quantity, totalPrice, scaleBasisId, bundleId , packageId , pcDefinitionLevelId);
        } else if (accessSequence.equalsIgnoreCase(Enums.AccessSequenceCode.ROUTE_PRODUCT.toString()) || accessSequence.equalsIgnoreCase(Enums.AccessSequenceCode.REGION_PRODUCT.toString())) {
            promoLimitDTO = this.GetPriceAgainstRouteProduct(priceConditionId, routeId, productDefinitionId, quantity, totalPrice, scaleBasisId, bundleId , packageId , pcDefinitionLevelId);
        } else if (accessSequence.equalsIgnoreCase(Enums.AccessSequenceCode.DISTRIBUTION_PRODUCT.toString())) {
            promoLimitDTO = this.GetPriceAgainstDistributionProduct(priceConditionId, distributionId, productDefinitionId, quantity, totalPrice, scaleBasisId, bundleId , packageId , pcDefinitionLevelId);
        } else if (accessSequence.equalsIgnoreCase(Enums.AccessSequenceCode.PRODUCT.toString())) {
            promoLimitDTO = GetPriceAgainstProduct(priceConditionId, productDefinitionId, quantity, totalPrice, scaleBasisId, bundleId , packageId , pcDefinitionLevelId);
        } else if (accessSequence.equalsIgnoreCase(Enums.AccessSequenceCode.OUTLET.toString())) {
            promoLimitDTO = this.GetPriceAgainstOutlet(priceConditionId, outletId, quantity, totalPrice, scaleBasisId);
        } else if (accessSequence.equalsIgnoreCase(Enums.AccessSequenceCode.ROUTE.toString())) {
            promoLimitDTO = this.GetPriceAgainstRoute(priceConditionId, routeId, quantity, totalPrice, scaleBasisId);
        } else if (accessSequence.equalsIgnoreCase(Enums.AccessSequenceCode.DISTRIBUTION.toString())) {
            promoLimitDTO = this.GetPriceAgainstDistribution(priceConditionId, distributionId, quantity, totalPrice, scaleBasisId);
        }

        return promoLimitDTO;
    }

    private PromoLimitDTO GetPriceAgainstProduct(int priceConditionId, int productDefinitionId,
                                                 int quantity, BigDecimal totalPrice, int scaleBasisId
            , Integer bundleId , Long packageId , Integer pcDefinitionLevelId) {

        PromoLimitDTO maxLimitDTO = new PromoLimitDTO();
       /* PriceConditionDetailsWithScale priceConditionDetailsWithScale = bundleId==null?priceConditionEntitiesDao
                .findPriceConditionDetail(priceConditionId,productDefinitionId)
                .subscribeOn(Schedulers.io()).blockingGet():priceConditionEntitiesDao
                .findPriceConditionDetailWithBundle(priceConditionId,productDefinitionId,bundleId)
                .subscribeOn(Schedulers.io()).blockingGet();*/

        PriceConditionDetailsWithScale priceConditionDetailsWithScale = priceConditionEntitiesDao
                .findPriceConditionDetailWithBundle(priceConditionId, productDefinitionId, bundleId , packageId)
                .subscribeOn(Schedulers.io()).blockingGet();
        if (priceConditionDetailsWithScale == null)
            return null;
        PriceConditionDetail detail = priceConditionDetailsWithScale.getPriceConditionDetail();
        if (detail != null) {
            maxLimitDTO.setPriceConditionDetailId(detail.getPriceConditionDetailId());
            maxLimitDTO.setLimitBy(detail.getLimitBy());
            maxLimitDTO.setMaximumLimit(detail.getMaximumLimit());

            Product unitProduct = productsDao.checkUnitProduct(productDefinitionId).subscribeOn(Schedulers.io()).blockingGet();
            boolean isProductUnit = true ;
            if (unitProduct == null)
                isProductUnit = false;


//            Product cartonProduct = productsDao.checkCartonProduct(productDefinitionId).subscribeOn(Schedulers.io()).blockingGet();

            if (pcDefinitionLevelId == (int) Enums.EnumPCDefinitionLevel.PackageLevel && !isProductUnit)
            {
                if (priceConditionDetailsWithScale.getPriceConditionScaleList().size() < 1)
                {
                    if (detail.getCartonAmount()!= null)
                    {
                        maxLimitDTO.setUnitPrice(BigDecimal.valueOf(detail.getCartonAmount()));
                    }
                }
                else if (priceConditionDetailsWithScale.getPriceConditionScaleList().size() > 0)
                {
                    maxLimitDTO.setUnitPrice(GetScaledAmount(priceConditionDetailsWithScale.getPriceConditionScaleList() , detail.getPriceConditionDetailId(), scaleBasisId, quantity, totalPrice, true));
                }
            }
            else
            {
                if (priceConditionDetailsWithScale.getPriceConditionScaleList().size() < 1)
                {
                    maxLimitDTO.setUnitPrice(detail.getAmount());
                }
                else if (priceConditionDetailsWithScale.getPriceConditionScaleList().size() > 0)
                {
                    maxLimitDTO.setUnitPrice(GetScaledAmount(priceConditionDetailsWithScale.getPriceConditionScaleList() , detail.getPriceConditionDetailId(), scaleBasisId, quantity, totalPrice , false));
                }
            }

//
//            if (priceConditionDetailsWithScale.getPriceConditionScaleList().size() < 1) {
//                maxLimitDTO.setUnitPrice(detail.getAmount());
//            } else if (priceConditionDetailsWithScale.getPriceConditionScaleList().size() > 0) {
//                BigDecimal unitPrice = GetScaledAmount(priceConditionDetailsWithScale.getPriceConditionScaleList(), detail.getPriceConditionDetailId(), scaleBasisId, quantity, totalPrice);
//                maxLimitDTO.setUnitPrice(unitPrice);
//            }
        }
        return maxLimitDTO;

    }

    private PromoLimitDTO GetPriceAgainstOutletProduct(int priceConditionId, int outletId, int productDefinitionId, int quantity, BigDecimal totalPrice, int scaleBasisId, Integer bundleId , Long packageId , Integer pcDefinitionLevelId) {

        PromoLimitDTO maxLimitDTO = new PromoLimitDTO();
        PriceConditionDetailsWithScale priceConditionDetailsWithScale = priceConditionEntitiesDao.findPriceConditionEntityOutlet(priceConditionId, outletId , bundleId)
                .flatMap(priceConditionEntities -> getPriceConditionDetailObservable(priceConditionEntities, priceConditionId, productDefinitionId, bundleId , packageId))
                .subscribeOn(Schedulers.io()).blockingGet();

        if (priceConditionDetailsWithScale == null)
            return null;

        PriceConditionDetail detail = priceConditionDetailsWithScale.getPriceConditionDetail();
        if (detail != null) {
            maxLimitDTO.setPriceConditionDetailId(detail.getPriceConditionDetailId());
            maxLimitDTO.setLimitBy(detail.getLimitBy());
            maxLimitDTO.setMaximumLimit(detail.getMaximumLimit());


            Product unitProduct = productsDao.checkUnitProduct(productDefinitionId).subscribeOn(Schedulers.io()).blockingGet();
             boolean isProductUnit = true ;
            if (unitProduct == null)
                isProductUnit = false;


//            Product cartonProduct = productsDao.checkCartonProduct(productDefinitionId).subscribeOn(Schedulers.io()).blockingGet();

            if (pcDefinitionLevelId == (int) Enums.EnumPCDefinitionLevel.PackageLevel && !isProductUnit)
            {
                if (priceConditionDetailsWithScale.getPriceConditionScaleList().size() < 1)
                {
                    if (detail.getCartonAmount()!= null)
                    {
                        maxLimitDTO.setUnitPrice(BigDecimal.valueOf(detail.getCartonAmount()));
                    }
                }
                else if (priceConditionDetailsWithScale.getPriceConditionScaleList().size() > 0)
                {
                    maxLimitDTO.setUnitPrice(GetScaledAmount(priceConditionDetailsWithScale.getPriceConditionScaleList() , detail.getPriceConditionDetailId(), scaleBasisId, quantity, totalPrice, true));
                }
            }
            else
            {
                if (priceConditionDetailsWithScale.getPriceConditionScaleList().size() < 1)
                {
                    maxLimitDTO.setUnitPrice(detail.getAmount());
                }
                else if (priceConditionDetailsWithScale.getPriceConditionScaleList().size() > 0)
                {
                    maxLimitDTO.setUnitPrice(GetScaledAmount(priceConditionDetailsWithScale.getPriceConditionScaleList() , detail.getPriceConditionDetailId(), scaleBasisId, quantity, totalPrice , false));
                }
            }


//            if (priceConditionDetailsWithScale.getPriceConditionScaleList().size() < 1) {
//                maxLimitDTO.setUnitPrice(detail.getAmount());
//            } else if (priceConditionDetailsWithScale.getPriceConditionScaleList().size() > 0) {
//                BigDecimal unitPrice = GetScaledAmount(priceConditionDetailsWithScale.getPriceConditionScaleList(), detail.getPriceConditionDetailId(), scaleBasisId, quantity, totalPrice);
//                maxLimitDTO.setUnitPrice(unitPrice);
//            }
        }
        return maxLimitDTO;

    }

    private PromoLimitDTO GetPriceAgainstRouteProduct(int priceConditionId, int routeId, int productDefinitionId, int quantity, BigDecimal totalPrice,
                                                      int scaleBasisId, Integer bundleId , Long packageId , Integer pcDefinitionLevelId) {

        PromoLimitDTO maxLimitDTO = new PromoLimitDTO();

        PriceConditionDetailsWithScale priceConditionDetail = priceConditionEntitiesDao.findPriceConditionEntityRoute(priceConditionId, routeId , bundleId)
                .flatMap(priceConditionEntities -> getPriceConditionDetailObservable(priceConditionEntities, priceConditionId, productDefinitionId, bundleId , packageId))
                .subscribeOn(Schedulers.io()).blockingGet();

        if (priceConditionDetail != null) {
            PriceConditionDetail detail = priceConditionDetail.getPriceConditionDetail();
            maxLimitDTO.setPriceConditionDetailId(detail.getPriceConditionDetailId());
            maxLimitDTO.setLimitBy(detail.getLimitBy());
            maxLimitDTO.setMaximumLimit(detail.getMaximumLimit());

            Product unitProduct = productsDao.checkUnitProduct(productDefinitionId).subscribeOn(Schedulers.io()).blockingGet();
            boolean isProductUnit = true ;
            if (unitProduct == null)
                isProductUnit = false;


//            Product cartonProduct = productsDao.checkCartonProduct(productDefinitionId).subscribeOn(Schedulers.io()).blockingGet();

            if (pcDefinitionLevelId == (int) Enums.EnumPCDefinitionLevel.PackageLevel && !isProductUnit)
            {
                if (priceConditionDetail.getPriceConditionScaleList().size() < 1)
                {
                    if (detail.getCartonAmount()!= null)
                    {
                        maxLimitDTO.setUnitPrice(BigDecimal.valueOf(detail.getCartonAmount()));
                    }
                }
                else if (priceConditionDetail.getPriceConditionScaleList().size() > 0)
                {
                    maxLimitDTO.setUnitPrice(GetScaledAmount(priceConditionDetail.getPriceConditionScaleList() , detail.getPriceConditionDetailId(), scaleBasisId, quantity, totalPrice, true));
                }
            }
            else
            {
                if (priceConditionDetail.getPriceConditionScaleList().size() < 1)
                {
                    maxLimitDTO.setUnitPrice(detail.getAmount());
                }
                else if (priceConditionDetail.getPriceConditionScaleList().size() > 0)
                {
                    maxLimitDTO.setUnitPrice(GetScaledAmount(priceConditionDetail.getPriceConditionScaleList() , detail.getPriceConditionDetailId(), scaleBasisId, quantity, totalPrice , false));
                }
            }


//            if (priceConditionDetail.getPriceConditionScaleList().size() < 1) {
//                maxLimitDTO.setUnitPrice(detail.getAmount());
//            } else if (priceConditionDetail.getPriceConditionScaleList().size() > 0) {
//                BigDecimal unitPrice = GetScaledAmount(priceConditionDetail.getPriceConditionScaleList(), detail.getPriceConditionDetailId(), scaleBasisId, quantity, totalPrice);
//                maxLimitDTO.setUnitPrice(unitPrice);
//            }
        }


        return maxLimitDTO;
    }

    private PromoLimitDTO GetPriceAgainstDistributionProduct(int priceConditionId, Integer distributionId, int productDefinitionId, int quantity, BigDecimal totalPrice,
                                                             int scaleBasisId, Integer bundleId , Long packageId , Integer pcDefinitionLevelId) {


        PromoLimitDTO maxLimitDTO = new PromoLimitDTO();

        PriceConditionDetailsWithScale priceConditionDetail = priceConditionEntitiesDao.findPriceConditionEntityDistribution(priceConditionId, distributionId , bundleId)
                .flatMap(priceConditionEntities -> getPriceConditionDetailObservable(priceConditionEntities, priceConditionId, productDefinitionId, bundleId , packageId ))
                .subscribeOn(Schedulers.io()).blockingGet();
        if (priceConditionDetail != null) {
            PriceConditionDetail detail = priceConditionDetail.getPriceConditionDetail();
            maxLimitDTO.setPriceConditionDetailId(detail.getPriceConditionDetailId());
            maxLimitDTO.setLimitBy(detail.getLimitBy());
            maxLimitDTO.setMaximumLimit(detail.getMaximumLimit());

            Product unitProduct = productsDao.checkUnitProduct(productDefinitionId).subscribeOn(Schedulers.io()).blockingGet();
            boolean isProductUnit = true ;
            if (unitProduct == null)
                isProductUnit = false;


//            Product cartonProduct = productsDao.checkCartonProduct(productDefinitionId).subscribeOn(Schedulers.io()).blockingGet();

            if (pcDefinitionLevelId == (int) Enums.EnumPCDefinitionLevel.PackageLevel && !isProductUnit)
            {
                if (priceConditionDetail.getPriceConditionScaleList().size() < 1)
                {
                    if (detail.getCartonAmount()!= null)
                    {
                        maxLimitDTO.setUnitPrice(BigDecimal.valueOf(detail.getCartonAmount()));
                    }
                }
                else if (priceConditionDetail.getPriceConditionScaleList().size() > 0)
                {
                    maxLimitDTO.setUnitPrice(GetScaledAmount(priceConditionDetail.getPriceConditionScaleList() , detail.getPriceConditionDetailId(), scaleBasisId, quantity, totalPrice, true));
                }
            }
            else
            {
                if (priceConditionDetail.getPriceConditionScaleList().size() < 1)
                {
                    maxLimitDTO.setUnitPrice(detail.getAmount());
                }
                else if (priceConditionDetail.getPriceConditionScaleList().size() > 0)
                {
                    maxLimitDTO.setUnitPrice(GetScaledAmount(priceConditionDetail.getPriceConditionScaleList() , detail.getPriceConditionDetailId(), scaleBasisId, quantity, totalPrice , false));
                }
            }


//            if (priceConditionDetail.getPriceConditionScaleList().size() < 1) {
//                maxLimitDTO.setUnitPrice(detail.getAmount());
//            } else if (priceConditionDetail.getPriceConditionScaleList().size() > 0) {
//                BigDecimal unitPrice = GetScaledAmount(priceConditionDetail.getPriceConditionScaleList(), detail.getPriceConditionDetailId(), scaleBasisId, quantity, totalPrice);
//                maxLimitDTO.setUnitPrice(unitPrice);
//            }
        }

        return maxLimitDTO;
    }

    private PromoLimitDTO GetPriceAgainstOutlet(int priceConditionId, int outletId, int quantity, BigDecimal totalPrice, int scaleBasisId) {
        PromoLimitDTO maxLimitDTO = new PromoLimitDTO();

        PriceConditionDetailsWithScale priceConditionDetail = priceConditionEntitiesDao.findPriceConditionDetails(priceConditionId, outletId)
                .subscribeOn(Schedulers.io()).blockingGet();
        if (priceConditionDetail != null) {
            PriceConditionDetail detail = priceConditionDetail.getPriceConditionDetail();
            maxLimitDTO.setPriceConditionDetailId(detail.getPriceConditionDetailId());
            maxLimitDTO.setLimitBy(detail.getLimitBy());
            maxLimitDTO.setMaximumLimit(detail.getMaximumLimit());
            if (priceConditionDetail.getPriceConditionScaleList().size() < 1) {
                maxLimitDTO.setUnitPrice(detail.getAmount());
            } else if (priceConditionDetail.getPriceConditionScaleList().size() > 0) {
                BigDecimal unitPrice = GetScaledAmount(priceConditionDetail.getPriceConditionScaleList(), detail.getPriceConditionDetailId(), scaleBasisId, quantity, totalPrice , false);
                maxLimitDTO.setUnitPrice(unitPrice);
            }
        }

        return maxLimitDTO;
    }

    private PromoLimitDTO GetPriceAgainstRoute(int priceConditionId, int routeId, int quantity, BigDecimal totalPrice, int scaleBasisId) {
        PromoLimitDTO maxLimitDTO = new PromoLimitDTO();

        PriceConditionDetailsWithScale priceConditionDetail = priceConditionEntitiesDao.findPriceConditionDetailsRoute(priceConditionId, routeId)
                .subscribeOn(Schedulers.io()).blockingGet();

        if (priceConditionDetail != null) {
            PriceConditionDetail detail = priceConditionDetail.getPriceConditionDetail();
            maxLimitDTO.setPriceConditionDetailId(detail.getPriceConditionDetailId());
            maxLimitDTO.setLimitBy(detail.getLimitBy());
            maxLimitDTO.setMaximumLimit(detail.getMaximumLimit());
            if (priceConditionDetail.getPriceConditionScaleList().size() < 1) {
                maxLimitDTO.setUnitPrice(detail.getAmount());
            } else if (priceConditionDetail.getPriceConditionScaleList().size() > 0) {
                BigDecimal unitPrice = GetScaledAmount(priceConditionDetail.getPriceConditionScaleList(), detail.getPriceConditionDetailId(), scaleBasisId, quantity, totalPrice , false);
                maxLimitDTO.setUnitPrice(unitPrice);
            }
        }
        return maxLimitDTO;
    }

    private PromoLimitDTO GetPriceAgainstDistribution(int priceConditionId, Integer distributionId, int quantity, BigDecimal totalPrice, int scaleBasisId) {
        PromoLimitDTO maxLimitDTO = new PromoLimitDTO();

        PriceConditionDetailsWithScale priceConditionDetail = priceConditionEntitiesDao.findPriceConditionDetailsDistribution(priceConditionId, distributionId)
                .subscribeOn(Schedulers.io()).blockingGet();

        if (priceConditionDetail != null) {
            PriceConditionDetail detail = priceConditionDetail.getPriceConditionDetail();
            maxLimitDTO.setPriceConditionDetailId(detail.getPriceConditionDetailId());
            maxLimitDTO.setLimitBy(detail.getLimitBy());
            maxLimitDTO.setMaximumLimit(detail.getMaximumLimit());
            if (priceConditionDetail.getPriceConditionScaleList().size() < 1) {
                maxLimitDTO.setUnitPrice(detail.getAmount());
            } else if (priceConditionDetail.getPriceConditionScaleList().size() > 0) {
                BigDecimal unitPrice = GetScaledAmount(priceConditionDetail.getPriceConditionScaleList(), detail.getPriceConditionDetailId(), scaleBasisId, quantity, totalPrice , false);
                maxLimitDTO.setUnitPrice(unitPrice);
            }
        }

        return maxLimitDTO;
    }


    private Maybe<PriceConditionDetailsWithScale> getPriceConditionDetailObservable(PriceConditionEntities priceConditionEntity
            , int priceConditionId, int productDefinitionId, Integer bundleId , Long packageId) {
        if (priceConditionEntity == null)
            return null;

        return priceConditionEntitiesDao.findPriceConditionDetailWithBundle(priceConditionId,
                productDefinitionId, bundleId , packageId)
                .subscribeOn(Schedulers.single());

    }


    private BigDecimal GetScaledAmount(List<PriceConditionScale> scaleList, int priceConditionDetailId, int scaleBasisId, int quantity, BigDecimal totalPrice , Boolean useCartonAmount) {
        BigDecimal returnAmount = BigDecimal.ZERO;
        if (scaleBasisId == Enums.ScaleBasis.Quantity || scaleBasisId == Enums.ScaleBasis.Total_Quantity) {
            Collections.sort(scaleList, (o1, o2) -> o2.getFrom().compareTo(o1.getFrom()));
            for (PriceConditionScale conditionScale : scaleList) {

                if (conditionScale.getPriceConditionDetailId() == priceConditionDetailId && conditionScale.getFrom() <= quantity) {
                    if (useCartonAmount){
                        if (conditionScale.getCartonAmount() != null)
                            returnAmount = BigDecimal.valueOf(conditionScale.getCartonAmount());
                    } else
                        returnAmount = conditionScale.getAmount();
                    break;
                }
            }

        } else if (scaleBasisId == Enums.ScaleBasis.Value) {
            Collections.sort(scaleList, (o1, o2) -> o2.getAmount().compareTo(o1.getAmount()));
            for (PriceConditionScale conditionScale : scaleList) {

                if (conditionScale.getPriceConditionDetailId() == priceConditionDetailId && conditionScale.getFrom() <= totalPrice.doubleValue()) {
                    if (useCartonAmount){
                        if (conditionScale.getCartonAmount() != null)
                            returnAmount = BigDecimal.valueOf(conditionScale.getCartonAmount());
                    } else
                        returnAmount = conditionScale.getAmount();
                    break;
                }
            }
        }
        return returnAmount;
    }


    PriceOutputDTO objPriceOutputDTO = new PriceOutputDTO();
    BigDecimal totalPrice = BigDecimal.ZERO; //input price for every condition class
    BigDecimal subTotal = BigDecimal.ZERO; //input price for every condition class

    boolean isPriceFound = false;

    public PriceOutputDTO getOrderItemPrice(
            List<ProductQuantity> productQuantityDTOList,
            int mobileOrderDetailId,
            int outletId, int productDefinitionId, int quantity, int routeId,
            Integer distributionId , List<CombinedMaxLimitHolderDTO> combinedMaxLimitHolderDTOList ,
            OrderResponseModel orderResponseModel , String date , Long productId) {

        // Added By Husnain

        //

        objPriceOutputDTO = new PriceOutputDTO();
        totalPrice = BigDecimal.ZERO; //input price for every condition class
        subTotal = BigDecimal.ZERO;
        isPriceFound = false;
        List<PriceConditionClass> conditionClasses = pricingDao.findPriceConditionClasses(1).subscribeOn(Schedulers.io()).blockingGet();

        for (PriceConditionClass conditionClass : conditionClasses) {
            List<PriceConditionType> conditionTypes = pricingDao.findPriceConditionTypes(conditionClass.getPriceConditionClassId())
                    .subscribeOn(Schedulers.io()).blockingGet();


            isPriceFound = false;

            for (PriceConditionType conditionType : conditionTypes) {
                List<PriceConditionWithAccessSequence> priceConditions;
                List<Integer> bundleIds = getBundlesList(productDefinitionId, conditionType.getPriceConditionTypeId());
                List<Integer> bundlesToApply = getBundlesToApply(bundleIds);
                List<PriceConditionWithAccessSequence> filterPriceConditions = new ArrayList<>();
                if (bundlesToApply.isEmpty()){
                    priceConditions = pricingDao
                            .getPriceConditionAndAccessSequenceByTypeId(conditionType.getPriceConditionTypeId() , orderResponseModel.getOutlet().getVpoClassificationId()
                                    , orderResponseModel.getOutlet().getPricingGroupId(), orderResponseModel.getChannelId() , orderResponseModel.getOrganizationId() , orderResponseModel.getOutlet().getOutletPromoConfigId(),
                                    orderResponseModel.getOutlet().getCustomerRegistrationTypeId() , date , distributionId , outletId).subscribeOn(Schedulers.io()).blockingGet();

                    for (PriceConditionWithAccessSequence priceConditionWithAccessSequence : priceConditions){

                        if ((priceConditionWithAccessSequence.getChannelAttributeCount()==0 || priceConditionWithAccessSequence.getOutletChannelAttribute()>0) &&
                                (priceConditionWithAccessSequence.getGroupAttributeCount()==0 || priceConditionWithAccessSequence.getOutletGroupAttribute()>0) &&
                                (priceConditionWithAccessSequence.getVPOClassificationAttributeCount()==0 || priceConditionWithAccessSequence.getOutletVPOClassificationAttributeCount()>0)){

                            filterPriceConditions.add(priceConditionWithAccessSequence);
                        }
                    }
                } else {
                    priceConditions = pricingDao
                            .getPriceConditionAndAccessSequenceByTypeIdWithBundle(conditionType.getPriceConditionTypeId(), bundlesToApply).subscribeOn(Schedulers.io()).blockingGet();
                }



                Collections.sort(filterPriceConditions, (o1, o2) -> o1.getOrder().compareTo(o2.getOrder()));
                for (PriceConditionWithAccessSequence prAccSeqDetail : filterPriceConditions) {

                    // added By Husnain
                    Long packageId = null;

                    for (ProductQuantity productQuantity : productQuantityDTOList){
                        if (productDefinitionId == productQuantity.getProductDefinitionId()){
                            packageId = productQuantity.getPackageId();
                        }
                    }

                    int pcDefinitionLevelId = (int) Enums.EnumPCDefinitionLevel.ProductLevel;
                    if (conditionType.getPcDefinitionLevelId() != null)
                    {
                        pcDefinitionLevelId = (int)conditionType.getPcDefinitionLevelId();
                    }

                    PromoLimitDTO limitDTO = GetPriceAgainstPriceCondition(prAccSeqDetail.getPriceConditionId(), prAccSeqDetail.getSequenceCode(),
                            outletId, productDefinitionId, quantity, totalPrice, conditionType.getPriceScaleBasisId(), routeId,
                            distributionId, prAccSeqDetail.getBundleId() , orderResponseModel.getChannelId() , packageId , pcDefinitionLevelId );


                    if (limitDTO != null && limitDTO.getUnitPrice().doubleValue() > -1) {
                        isPriceFound = true;
                        UnitPriceBreakDown objSingleBlock = new UnitPriceBreakDown();

                        objSingleBlock.setPriceConditionDetailId(limitDTO.getPriceConditionDetailId());
                        objSingleBlock.setOrderDetailId(mobileOrderDetailId);

                        CombinedMaxLimitHolderDTO existingCombinedLimit  = new CombinedMaxLimitHolderDTO();
                        if (pcDefinitionLevelId == (int) Enums.EnumPCDefinitionLevel.PackageLevel || conditionType.getLRB() ) // Add LRB Check
                        {
                            if (prAccSeqDetail.getCombinedLimitBy() != null)
                            {
                                if (combinedMaxLimitHolderDTOList.size() == 0)
                                    existingCombinedLimit = null;
                                for (CombinedMaxLimitHolderDTO combinedMaxLimitHolderDTO : combinedMaxLimitHolderDTOList){
                                    if (combinedMaxLimitHolderDTO.getPriceConditionId() == prAccSeqDetail.getPriceConditionId()){
                                        existingCombinedLimit = combinedMaxLimitHolderDTO;
                                        break;
                                    }
                                }
                            }
                            else
                            {
                                if (combinedMaxLimitHolderDTOList.size() == 0)
                                    existingCombinedLimit = null;
                                for (CombinedMaxLimitHolderDTO combinedMaxLimitHolderDTO : combinedMaxLimitHolderDTOList){
                                    if (combinedMaxLimitHolderDTO.getPriceConditionId() == prAccSeqDetail.getPriceConditionId() && packageId.equals(Long.valueOf(combinedMaxLimitHolderDTO.getPackageId()))){
                                        existingCombinedLimit = combinedMaxLimitHolderDTO;
                                        break;
                                    }
                                }
                            }

                            if (existingCombinedLimit == null)
                            {
                                existingCombinedLimit = new CombinedMaxLimitHolderDTO();
                                existingCombinedLimit.setPackageId(packageId.intValue());
                                existingCombinedLimit.setPriceConditionId(prAccSeqDetail.getPriceConditionId());
                                existingCombinedLimit.setAvailedAmount(0.0);
                                existingCombinedLimit.setAvailedQuantity(0);
                                existingCombinedLimit.setPriceConditionAppliedForTheFirstItem(true);

                            }
                            if (prAccSeqDetail.getCombinedLimitBy() != null)
                            {
                                limitDTO.setLimitBy(prAccSeqDetail.getCombinedLimitBy());
                            }
                            if (limitDTO.getLimitBy() == (int) Enums.LimitBy.Amount && prAccSeqDetail.getCombinedMaxValueLimit() != null)
                            {
                                limitDTO.setMaximumLimit(BigDecimal.valueOf(prAccSeqDetail.getCombinedMaxValueLimit()));
                            }
                            if (limitDTO.getLimitBy() == (int) Enums.LimitBy.Quantity && prAccSeqDetail.getCombinedMaxCaseLimit() != null)
                            {
                                limitDTO.setMaximumLimit(BigDecimal.valueOf(prAccSeqDetail.getCombinedMaxCaseLimit()));
                            }
                        }
                        else
                        {
                            existingCombinedLimit = null;

                        }

                        if (limitDTO.getLimitBy() != null) {


                            if (limitDTO.getMaximumLimit() != null)
                                objSingleBlock.setMaximumLimit(limitDTO.getMaximumLimit().doubleValue());
                            objSingleBlock.setLimitBy(limitDTO.getLimitBy());

                            if (objSingleBlock.getAlreadyAvailed() == null)
                            {
                                objSingleBlock.setAlreadyAvailed(0.0);
                            }

                            OutletAvailedPromotion objAlreadyAvailed = pricingDao.getAlreadyAvailedPromo(outletId , prAccSeqDetail.getPriceConditionId() , objSingleBlock.getPriceConditionDetailId() , productDefinitionId , productId).subscribeOn(Schedulers.io()).blockingGet();
                        /*   var objAlreadyAvailed = _outletAvailedPromotionDataHandler.GetAlreadyAvailedValue(objSingleBlock.getPriceConditionDetailId());
//                            }*/

                            if(objAlreadyAvailed != null){
                                if (objAlreadyAvailed.getAmount() != null)
                                {
                                    if (objSingleBlock.getLimitBy() == (int) Enums.LimitBy.Amount)
                                    {
                                        objSingleBlock.setAlreadyAvailed(objAlreadyAvailed.getAmount());
                                    }
                                    else
                                    {
                                        objSingleBlock.setAlreadyAvailed(objAlreadyAvailed.getQuantity().doubleValue());
                                    }
                                }

                                if (pcDefinitionLevelId == (int) Enums.EnumPCDefinitionLevel.PackageLevel || (conditionType.getLRB() != null && conditionType.getLRB() ) )
                                {
//                                    if (existingCombinedLimit.getAvailedAmount() != null)
//                                    {
//                                        objSingleBlock.setAlreadyAvailed(objSingleBlock.getAlreadyAvailed() + existingCombinedLimit.getAvailedAmount());
//                                    }

                                    if (limitDTO.getLimitBy() == (int) Enums.LimitBy.Amount && existingCombinedLimit.getAvailedAmount() != null)
                                    {
                                        objSingleBlock.setAlreadyAvailed(objSingleBlock.getAlreadyAvailed() + existingCombinedLimit.getAvailedAmount());;
                                    }
                                    else if (limitDTO.getLimitBy() == (int)Enums.LimitBy.Quantity && existingCombinedLimit.getAvailedQuantity() != null)
                                    {
                                        objSingleBlock.setAlreadyAvailed(objSingleBlock.getAlreadyAvailed() + existingCombinedLimit.getAvailedQuantity());
                                    }



                                    if (prAccSeqDetail.getCombinedLimitBy() == (int) Enums.LimitBy.Amount)
                                    {
                                        objSingleBlock.setMaximumLimit(prAccSeqDetail.getCombinedMaxValueLimit());
                                    }
                                    else if (prAccSeqDetail.getCombinedLimitBy() == (int) Enums.LimitBy.Quantity)
                                    {
                                        objSingleBlock.setMaximumLimit(prAccSeqDetail.getCombinedMaxCaseLimit());
                                    }
                                }
                            }

                        }

                        Double inputAmount = null;

                        if (objPriceOutputDTO.getPriceBreakdown() != null){

                            for (UnitPriceBreakDown unitPriceBreakDown : objPriceOutputDTO.getPriceBreakdown()){

                                if (unitPriceBreakDown.getmPriceConditionClassId() == conditionClass.getDeriveFromConditionClassId()){
                                    inputAmount = unitPriceBreakDown.getTotalPrice();
                                    break;
                                }
                            }
                        }

                        if (inputAmount == null)
                        {
                            inputAmount = totalPrice.toBigInteger().doubleValue();
                        }

                        ItemAmountDTO blockPrice = CalculateBlockPrice(inputAmount , limitDTO.getUnitPrice().doubleValue() , quantity , conditionType.getPriceScaleBasisId() , conditionType.getOperationType() , conditionType.getCalculationType() , conditionType.getRoundingRule() , objSingleBlock.getLimitBy() , objSingleBlock.getMaximumLimit(), objSingleBlock.getAlreadyAvailed() , totalPrice.doubleValue());

                        subTotal = subTotal.add(BigDecimal.valueOf(blockPrice.getBlockPrice()));
                        totalPrice = BigDecimal.valueOf(blockPrice.getTotalPrice());
                        objSingleBlock.mPriceConditionType = conditionType.getName();
                        objSingleBlock.mPriceConditionClass = conditionClass.getName();
                        objSingleBlock.mPriceConditionClassOrder = conditionClass.getOrder();
                        objSingleBlock.mPriceCondition = prAccSeqDetail.getName();
                        objSingleBlock.mAccessSequence = prAccSeqDetail.getSequenceName();
                        objSingleBlock.mCalculationType = conditionType.getCalculationType();
                        objSingleBlock.mUnitPrice = limitDTO.getUnitPrice().floatValue();
                        objSingleBlock.mBlockPrice = blockPrice.getBlockPrice();
                        objSingleBlock.mTotalPrice = totalPrice.doubleValue();

                        objSingleBlock.mPriceConditionId = prAccSeqDetail.getPriceConditionId();
                        objSingleBlock.mProductDefinitionId = productDefinitionId;
                        objSingleBlock.setmPriceConditionClassId(conditionClass.getPriceConditionClassId());


                        objPriceOutputDTO.getPriceBreakdown().add(objSingleBlock);
                        objPriceOutputDTO.setTotalPrice(totalPrice);

                        if (blockPrice.IsMaxLimitReached) {
                            Message message = new Message();
                            {
                                message.setMessageSeverityLevel(conditionClass.getSeverityLevel());
                                message.setMessageText("Max limit crossed for " + objSingleBlock.getPriceCondition());
                            }
                            objPriceOutputDTO.getMessages().add(message);
                        }


                        if (existingCombinedLimit != null)
                        {
                            existingCombinedLimit.setAvailedAmount(existingCombinedLimit.getAvailedAmount() != null ? existingCombinedLimit.getAvailedAmount()+blockPrice.BlockPrice : blockPrice.BlockPrice);
                            if (blockPrice.getActualQuantity() != null)
                            {
                                existingCombinedLimit.setAvailedQuantity(existingCombinedLimit.getAvailedQuantity()!= null ? existingCombinedLimit.getAvailedQuantity()+  blockPrice.getActualQuantity() : blockPrice.getActualQuantity());
                            }
                            if (existingCombinedLimit.getPriceConditionAppliedForTheFirstItem() !=null && existingCombinedLimit.getPriceConditionAppliedForTheFirstItem())
                            {
                                existingCombinedLimit.setPriceConditionAppliedForTheFirstItem(false);
                                combinedMaxLimitHolderDTOList.add(existingCombinedLimit);
                            }
                        }

                        break;
                    }

                }
            }
            if (!isPriceFound && !TextUtils.isEmpty(conditionClass.getSeverityLevelMessage())
                    && conditionClass.getSeverityLevel() != Enums.MessageSeverityLevel.MESSAGE) {
                Message message = new Message();
                message.MessageSeverityLevel = conditionClass.getSeverityLevel();
                message.MessageText = conditionClass.getSeverityLevelMessage();
                objPriceOutputDTO.Messages.add(message);
            }
        }
        objPriceOutputDTO.setTotalPrice(totalPrice);
        return objPriceOutputDTO;
    }



    private Completable addProductQty(List<ProductQuantity> productQuantities) {
        return Completable.fromAction(() -> pricingDao.insertTempOrderQty(productQuantities));
    }


    // calculate bundle
    private List<Integer> getBundlesList(int productDefId, int conditionTypeId) {

        return pricingDao.getBundleIdsForConditionType(productDefId, conditionTypeId).subscribeOn(Schedulers.io()).blockingGet();

    }

    private List<Integer> getBundlesToApply(List<Integer> bundleIds) {
        List<Integer> bundlesHolder = new ArrayList<>();
        for (Integer bundleId : bundleIds) {
            Integer minimumQty = pricingDao.getBundleMinQty(bundleId).blockingGet();
            int bundleProductCount = pricingDao.getBundleProductCount(bundleId).blockingGet();
            int calculatedBundleProdCount = pricingDao.getCalculatedBundleProdCount(bundleId).blockingGet();
            int bundleProdTotalQty = pricingDao.getBundleProdTotalQty(bundleId).blockingGet();
            if ((minimumQty == 0 || minimumQty <= bundleProdTotalQty)
                    && (calculatedBundleProdCount == bundleProductCount)
                    && (calculatedBundleProdCount > 0)) {
                bundlesHolder.add(bundleId);
            }
        }

        return bundlesHolder;

    }


//    region "Total Price Calculation"

    private ItemAmountDTO CalculateBlockPrice(Double inputAmount , Double amount, int quantity, int scaleBasisId, int operationType, int calculationType, int roundingRule, Integer limitBy, Double maxLimit, Double alreadyAvailed , Double totalPrice) {

        ItemAmountDTO objReturnPrice = new ItemAmountDTO();
        double blockPrice = 0;
        int actualQuantity = quantity;
        if (limitBy == null) {
            limitBy = 0;
        }

        if (limitBy == Enums.LimitBy.Quantity) {
            if (alreadyAvailed == null) {
                alreadyAvailed = 0.0;
            }
            int remainingQuantity = maxLimit.intValue() - alreadyAvailed.intValue();
            if (remainingQuantity < actualQuantity) {
                actualQuantity = remainingQuantity;
                objReturnPrice.IsMaxLimitReached = true;
            }
            objReturnPrice.setActualQuantity(actualQuantity);
        }


        if (scaleBasisId == (int) Enums.ScaleBasis.Quantity) {
            objReturnPrice.TotalPrice = 0;
            objReturnPrice.BlockPrice = amount * actualQuantity;
            blockPrice = amount * actualQuantity;
        } else if (scaleBasisId == (int) Enums.ScaleBasis.Value || scaleBasisId == (int) Enums.ScaleBasis.Total_Quantity) {
            objReturnPrice.TotalPrice = 0;
            objReturnPrice.BlockPrice = amount;
            blockPrice = amount;
        }


        if (calculationType == Enums.CalculationType.Fix) {
            if (limitBy == Enums.LimitBy.Amount) {
                blockPrice = getRemainingBlockPrice(blockPrice, maxLimit, alreadyAvailed); // amount replace blockprice
                if (blockPrice < objReturnPrice.getBlockPrice()) { // amount replace get block price
                    objReturnPrice.IsMaxLimitReached = true;
                    objReturnPrice.setBlockPrice(blockPrice);
                }
            }
            if (operationType == Enums.OperationType.Plus) {
                objReturnPrice.TotalPrice = totalPrice + blockPrice;
            } else if (operationType == Enums.OperationType.Minus) {
                objReturnPrice.TotalPrice = totalPrice - blockPrice;
            }
        } else if (calculationType == Enums.CalculationType.Percentage) {
            double value = (inputAmount * amount) / 100; //If percentage, use the same amount instead of amount * quantity
            double actualValue = value;
            if (limitBy == (int) Enums.LimitBy.Amount) {
                actualValue = getRemainingBlockPrice(value, maxLimit, alreadyAvailed);
                if (actualValue < value) {
                    objReturnPrice.IsMaxLimitReached = true;
                }
            }
            objReturnPrice.BlockPrice = actualValue;
            if (operationType == (int) Enums.OperationType.Plus) {
                objReturnPrice.TotalPrice = totalPrice + actualValue;
            } else if (operationType == Enums.OperationType.Minus) {
                objReturnPrice.TotalPrice = totalPrice - actualValue;
            }
        }

        if (roundingRule == Enums.RoundingRule.Zero_Decimal_Precision) {
            objReturnPrice.TotalPrice = (int) DecimalFormatter.round(objReturnPrice.TotalPrice, 0);
        } else if (roundingRule == Enums.RoundingRule.Two_Decimal_Precision) {
            objReturnPrice.TotalPrice = DecimalFormatter.round(objReturnPrice.TotalPrice, 2);
        } else if (roundingRule == (int) Enums.RoundingRule.Ceiling) {
            objReturnPrice.TotalPrice = (int) Math.ceil(objReturnPrice.TotalPrice);
        } else if (roundingRule == Enums.RoundingRule.Floor) {
            objReturnPrice.TotalPrice = (int) Math.floor(objReturnPrice.TotalPrice);
        }

        return objReturnPrice;
    }

    private double getRemainingBlockPrice(double amount, Double maxLimit, Double alreadyAvailed) {
        if (alreadyAvailed == null) {
            alreadyAvailed = 0.0;
        }
        if (maxLimit == null) {
            maxLimit = 0.0;
        }
        double remainingAmount = maxLimit - alreadyAvailed;
        if (remainingAmount < amount) {
            amount = remainingAmount;
        }
        return amount;
    }


    // region "Free Goods"

//    public List<FreeGoodOutputDTO> GetFreeGoods(int outletId, int routeId, int channelId, int distributionId, int productDefinitionId, Date OrderDate, List<ProductQuantity> productList, List<Integer> appliedFreeGoodGroupIds, int orderId) {
//        String predicate = "PricingTypeId = 2";
//        List<PriceAccessSequence> accessSequenceList = pricingDao.getAccessSequence().blockingGet();
//        List<FreeGoodOutputDTO> freeGoodDTOList = new ArrayList<>();
//        List<prGetFreeGoods> prfreeGoodsList = new ArrayList<>();
//        List<ProductQuantity> udtProductList = new ArrayList<>(productList);
//
//
//     /*   for (PriceAccessSequence sequence : accessSequenceList) {
//            if (sequence.getSequenceCode().equalsIgnoreCase(Enums.AccessSequenceCode.OUTLET_PRODUCT.toString())) {
//                prfreeGoodsList = _freeGoodMasterRepository.prGetFreeGoods(outletId, 0, 0, 0, productDefinitionId, OrderDate, udtProductList, sequence.getPriceAccessSequenceId());
//
//            } else if (sequence.getSequenceCode().equalsIgnoreCase(Enums.AccessSequenceCode.ROUTE_PRODUCT.toString())) {
//                prfreeGoodsList = _freeGoodMasterRepository.prGetFreeGoods(0, routeId, 0, 0, productDefinitionId, OrderDate, udtProductList, sequence.getPriceAccessSequenceId());
//            } else if (sequence.getSequenceCode().equalsIgnoreCase(Enums.AccessSequenceCode.DISTRIBUTION_PRODUCT.toString())) {
//                prfreeGoodsList = _freeGoodMasterRepository.prGetFreeGoods(0, 0, 0, distributionId, productDefinitionId, OrderDate, udtProductList, sequence.getPriceAccessSequenceId());
//            } else if (sequence.getSequenceCode().equalsIgnoreCase(Enums.AccessSequenceCode.PRODUCT.toString())) {
//                prfreeGoodsList = _freeGoodMasterRepository.prGetFreeGoods(0, 0, 0, 0, productDefinitionId, OrderDate, udtProductList, sequence.getPriceAccessSequenceId());
//            }
//            if (prfreeGoodsList.size() > 0) {
//                //check if the FreeGoodGroup already applied
//                var alreadyApplied = prfreeGoodsList.Where(x = > x.FreeGoodGroupId == appliedFreeGoodGroupIds.Where(y = > y == x.FreeGoodGroupId).
//                FirstOrDefault()).Count();
//
//                if (alreadyApplied == 0) {
//                    prGetFreeGoods promo = new prGetFreeGoods();
//
//                    var bundles = prfreeGoodsList.Where(x = > x.IsBundle == true)
//                    ; //if there exist bundle in the freegoodsList than only apply the single/First bundle offer and do not apply the remaining freegoods/Promos.
//                    if (bundles.Count() > 0) {
//                        promo = bundles.FirstOrDefault();
//                    } else {
//                        promo = prfreeGoodsList.FirstOrDefault();
//                    }
//
//
//                    var freeGoodOutputList = this.GetAvailableFreeGoods(promo, productList, outletId, orderId);
//
//                    freeGoodDTOList.AddRange(freeGoodOutputList);
//                    break;
//                } else {
//                    break;
//                }
//            }
//        }
//*/
//        return freeGoodDTOList;
//    }


    public OrderResponseModel GetFreeGoods(OrderResponseModel orderVM , String date)
    {
        try
        {
            if (orderVM.getOutletId() > 0)
            {

//                var orderDate = Convert.ToDateTime(orderVM.OrderDate);

                List<ProductQuantity> ProductList = new ArrayList<>();

//                var paidOrderDetails = orderVM.OrderDetails.Where(x => x.Type == "paid" && x.IsDeleted != true);

                List<OrderDetail> paidOrderDetails = new ArrayList<>();

                for (OrderDetail orderDetail: orderVM.getOrderDetails()) {

                    if (orderDetail.getType().equals("paid")){
                        paidOrderDetails.add(orderDetail);

                        ProductQuantity productQuantity = new ProductQuantity();
                        productQuantity.setProductDefinitionId(orderDetail.getProductTempDefId());
                        productQuantity.setQuantity(Integer.valueOf(orderDetail.getProductTempQuantity()));
                        productQuantity.setPackageId(orderDetail.getPkgId());
                        ProductList.add(productQuantity);
                    }
                }


                List<Integer> AppliedFreeGoodGroupIds = new ArrayList();
                for (OrderDetail orderDetail : paidOrderDetails)
                {
                    Product IsParentUnitProduct  = productsDao.checkUnitProduct(orderDetail.getProductTempDefId()).subscribeOn(Schedulers.io()).blockingGet();

                    Integer optionalFreeGoodCount = 0;

                    List<FreeGoodOutputDTO> freeGoodOutputDTOS = GetFreeGoods(orderVM.getOutletId() , orderVM.getChannelId() , orderVM.getOutlet().getVpoClassificationId(), orderVM.getOutlet().getPricingGroupId() , orderVM.getRouteId() , orderVM.getDistributionId() , orderDetail.getProductTempDefId() , ProductList , AppliedFreeGoodGroupIds , date);
                    for(FreeGoodOutputDTO freegood :freeGoodOutputDTOS){
                        freegood.setParentId(orderDetail.orderDetailId);
                        //freegood.Type="freegood";
                        AppliedFreeGoodGroupIds.add(freegood.getFreeGoodGroupId());
                        OrderDetail childOrderDetail = new OrderDetail();
                        childOrderDetail.setProductName(freegood.getProductName());
                        childOrderDetail.setProductTempDefId(freegood.getProductDefinitionId());
                        childOrderDetail.setProductId(freegood.getProductId().longValue());


                        //childOrderDetail.productsize=freegood.ProductSize
                        childOrderDetail.setType("freegood");

                        if (freegood.getFreeQuantityTypeId() == (int) Enums.EnumFreeGoodsQuantityType.Optional)
                            optionalFreeGoodCount++;

                        Product IsChildUnitProduct  = productsDao.checkUnitProduct(freegood.getProductDefinitionId()).subscribeOn(Schedulers.io()).blockingGet();



                        if (IsChildUnitProduct == null) //child is carton
                        {
                            childOrderDetail.setCartonDefinitionId(freegood.getProductDefinitionId());
                            childOrderDetail.setCartonQuantity(freegood.getFinalFreeGoodsQuantity());
                            childOrderDetail.setActualCartonStock(freegood.getStockInHand());
                            if (freegood.getFreeGoodGroupId() != null)
                            childOrderDetail.setCartonFreeGoodGroupId(freegood.getFreeGoodGroupId().longValue());
                            if (freegood.getFreeGoodDetailId() != null)
                            childOrderDetail.setCartonFreeGoodDetailId(freegood.getFreeGoodDetailId().longValue());
                            if (freegood.getFreeGoodExclusiveId() != null)
                            childOrderDetail.setCartonFreeGoodExclusiveId(freegood.getFreeGoodExclusiveId().longValue());
                            childOrderDetail.setCartonFreeQuantityTypeId(freegood.getFreeQuantityTypeId());
                            childOrderDetail.setCartonFreeGoodQuantity(freegood.getFinalFreeGoodsQuantity());
                            childOrderDetail.setCartonCode(freegood.getDefinitionCode());
//                            childOrderDetail.MaximumFreeGoodQuantity(freegood.getFreeGoodQuantity());

                        }
                        else //child is unit
                        {

                            childOrderDetail.setUnitCode(freegood.getDefinitionCode());
                            childOrderDetail.setUnitDefinitionId(freegood.getProductDefinitionId());
                            childOrderDetail.setUnitQuantity(freegood.getFinalFreeGoodsQuantity());
                            childOrderDetail.setActualUnitStock(freegood.getStockInHand());
                            if (freegood.getFreeGoodGroupId() != null)
                            childOrderDetail.setUnitFreeGoodGroupId(freegood.getFreeGoodGroupId().longValue());
                            if (freegood.getFreeGoodDetailId() != null)
                            childOrderDetail.setUnitFreeGoodDetailId(freegood.getFreeGoodDetailId().longValue());
                            if (freegood.getFreeGoodExclusiveId() != null)
                            childOrderDetail.setUnitFreeGoodExclusiveId(freegood.getFreeGoodExclusiveId().longValue());
                            childOrderDetail.setUnitFreeQuantityTypeId(freegood.getFreeQuantityTypeId());
                            childOrderDetail.setUnitFreeGoodQuantity(freegood.getFinalFreeGoodsQuantity());
//                            childOrderDetail.MaximumFreeGoodQuantity(freegood.getFreeGoodQuantity());


                        }


                        if (IsParentUnitProduct == null)//Parent is carton
                        {
                            if(orderDetail.getCartonOrderDetailId() != null)
                                childOrderDetail.setParentId(orderDetail.getCartonOrderDetailId().intValue());

                            List<OrderDetail> cartonFreeGoods;
                            cartonFreeGoods = orderDetail.getCartonFreeGoods();
                            cartonFreeGoods.add(childOrderDetail);

                            orderDetail.setCartonFreeGoods(cartonFreeGoods);
                        }
                        else //Parent ia unit
                        {
                            if(orderDetail.getUnitOrderDetailId() != null)
                                childOrderDetail.setParentId(orderDetail.getUnitOrderDetailId().intValue());

                            List<OrderDetail> unitFreeGoods;
                            unitFreeGoods = orderDetail.getUnitFreeGoods();
                            unitFreeGoods.add(childOrderDetail);

                            orderDetail.setUnitFreeGoods(unitFreeGoods);

                        }


//                        childOrderDetail.setMessages()









                        }

//
//                        if (freegood.FreeGoodTypeId == (int)EnumFreeGoodsType.Exclusive)
//                        {
//                            orderDetailVM.MaximumFreeGoodQuantity = childOrderDetail.MaximumFreeGoodQuantity;
//                        }
//                    }

                    if (optionalFreeGoodCount > 0)
                    {
                        if (IsParentUnitProduct == null){
                            orderDetail.setCartonFreeQuantityTypeId((int) Enums.EnumFreeGoodsQuantityType.Optional);
                           if (freeGoodOutputDTOS.size() > 0)
                            orderDetail.setCartonFreeGoodQuantity(freeGoodOutputDTOS.get(0).getFreeGoodQuantity());
                        }else{
                            orderDetail.setUnitFreeQuantityTypeId((int) Enums.EnumFreeGoodsQuantityType.Optional);
                            if (freeGoodOutputDTOS.size() > 0)
                                orderDetail.setUnitFreeGoodQuantity(freeGoodOutputDTOS.get(0).getFreeGoodQuantity());
                        }
                    }
                    else
                    {
                        if (IsParentUnitProduct == null){
                            orderDetail.setCartonFreeQuantityTypeId((int) Enums.EnumFreeGoodsQuantityType.Primary);
                        }else{
                            orderDetail.setUnitFreeQuantityTypeId((int) Enums.EnumFreeGoodsQuantityType.Primary);
                        }
                    }

                }

                return orderVM;
                //return freegoods;
            }
            else
            {
                return orderVM;
            }
        }
        catch (Exception ex)
        {
            throw ex;
        }
    }

    public List<FreeGoodOutputDTO> GetFreeGoods(Integer OutletId , Integer channelId , Integer vpoClassficationId , Integer pricingGroupId , Integer RouteId ,  Integer DistributionId, Integer ProductDefinitionId,  List<ProductQuantity> ProductList, List<Integer> AppliedFreeGoodGroupIds , String date)
    {
        List<PriceAccessSequence> priceAccessSequence = pricingDao.getAccessSequenceByTypeId().subscribeOn(Schedulers.io()).blockingGet();

        List<FreeGoodOutputDTO> freeGoodDTOList = new ArrayList<>();
        List<FreeGoodGroups> appliedFreeGoodGroups = new ArrayList<>();
//        List<prGetFreeGoods> prfreeGoodsList = new ArrayList<>();

        for (PriceAccessSequence sequence  :priceAccessSequence)
        {
            if (sequence.getSequenceCode().toLowerCase().equals(Enums.AccessSequenceCode.OUTLET_PRODUCT.toString().toLowerCase()))
            {

                appliedFreeGoodGroups = pricingDao.appliedFreeGoodGroups(OutletId , channelId , vpoClassficationId , pricingGroupId , 0 , 0  , ProductDefinitionId , sequence.getPriceAccessSequenceId(), OutletId ).subscribeOn(Schedulers.io()).blockingGet();
                //prfreeGoodsList = _freeGoodMasterRepository.prGetFreeGoods(OutletId, 0, 0, 0, ProductId, ProductDefinitionId, Quantity, OrderDate, udtProductList);
//                prfreeGoodsList = _freeGoodMasterRepository.prGetFreeGoods(OutletId, 0, 0, 0, ProductDefinitionId, OrderDate, udtProductList, sequence.AccessSequenceId, OutletId);

            }
            else if (sequence.getSequenceCode().toLowerCase().equals(Enums.AccessSequenceCode.ROUTE_PRODUCT.toString().toLowerCase()))
            {
                appliedFreeGoodGroups = pricingDao.appliedFreeGoodGroups(0 , channelId , vpoClassficationId , pricingGroupId , RouteId , 0, ProductDefinitionId , sequence.getPriceAccessSequenceId(), OutletId).subscribeOn(Schedulers.io()).blockingGet();
//                prfreeGoodsList = _freeGoodMasterRepository.prGetFreeGoods(0, RouteId, 0, 0, ProductDefinitionId, OrderDate, udtProductList, sequence.AccessSequenceId, OutletId);
            }

            else if (sequence.getSequenceCode().toLowerCase().equals(Enums.AccessSequenceCode.DISTRIBUTION_PRODUCT.toString().toLowerCase()))
            {
                appliedFreeGoodGroups = pricingDao.appliedFreeGoodGroups(0 , channelId , vpoClassficationId , pricingGroupId , 0 , DistributionId , ProductDefinitionId , sequence.getPriceAccessSequenceId(), OutletId).subscribeOn(Schedulers.io()).blockingGet();
//                prfreeGoodsList = _freeGoodMasterRepository.prGetFreeGoods(0, 0, 0, DistributionId, ProductDefinitionId, OrderDate, udtProductList, sequence.AccessSequenceId, OutletId);
            }
            else if (sequence.getSequenceCode().toLowerCase().equals(Enums.AccessSequenceCode.PRODUCT.toString().toLowerCase()))
            {
                appliedFreeGoodGroups = pricingDao.appliedFreeGoodGroups(0 , channelId , vpoClassficationId , pricingGroupId, 0 , 0  , ProductDefinitionId , sequence.getPriceAccessSequenceId(), OutletId).subscribeOn(Schedulers.io()).blockingGet();


                //                prfreeGoodsList = _freeGoodMasterRepository.prGetFreeGoods(0, 0, 0, 0, ProductDefinitionId, OrderDate, udtProductList, sequence.AccessSequenceId, OutletId);

            }

            List<FreeGoodGroups> prfreeGoodsList = new ArrayList<>();


            for (FreeGoodGroups freeGoodGroups : appliedFreeGoodGroups){

                if (freeGoodGroups.getId() != null){
                    Integer bundeleProductsQuantitySum, //to hold the sum of the quantity of the products which exist in the current bundle
                            freeGoodDetailsCount , //count of products in freeGood promo
                            freeGoodOrderedProductCount ;//count of those ordered products which fall under the freegood promo

                    freeGoodDetailsCount = pricingDao.getFreeGoodDetailCount(freeGoodGroups.getId()).subscribeOn(Schedulers.io()).blockingGet();

                    List<FreeGoodDetails> freeGoodDetails= pricingDao.getFreeGoodDetail(freeGoodGroups.getId()).subscribeOn(Schedulers.io()).blockingGet();

                    freeGoodOrderedProductCount = 0 ;//ProductList.size();
                    Integer MaximumQuantity =pricingDao.getFreeGoodGroupMaxQuantity(freeGoodGroups.getId()).subscribeOn(Schedulers.io()).blockingGet();
                    //freeGoodGroups.getFreeQuantityTypeId();



                    for (ProductQuantity productQuantity : ProductList){

                        for (FreeGoodDetails freeGoodDetails1 : freeGoodDetails){
                            if (productQuantity.getProductDefinitionId() == freeGoodDetails1.getProductDefinitionId()){
                                freeGoodOrderedProductCount++;
                            }
                        }
                    }

                    if(freeGoodDetailsCount.equals(freeGoodOrderedProductCount)){

//                    for (ProductQuantity productQuanity : ProductList ){
//
//                        bundleProductsQuantitySum = bundleProductsQuantitySum + productQuanity.getQuantity();
//                    }
                        Integer bundleProductsQuantitySum = 0;

                        for (ProductQuantity productQuantity : ProductList){
                            for (FreeGoodDetails freeGoodDetails1 : freeGoodDetails){
                                if (productQuantity.getProductDefinitionId() == freeGoodDetails1.getProductDefinitionId()){
                                    bundleProductsQuantitySum = bundleProductsQuantitySum + productQuantity.getQuantity();
                                }
                            }
                        }





                        if(bundleProductsQuantitySum>=freeGoodGroups.getMinimumQuantity()  &&
                                (freeGoodGroups.getChannelAttributeCount()==0 || freeGoodGroups.getOutletChannelAttributeCount()>0) &&
                                (freeGoodGroups.getGroupAttributeCount()==0 || freeGoodGroups.getOutletGroupAttributeCount()>0) &&
                                (freeGoodGroups.getVpoAttributeCount()==0 || freeGoodGroups.getOutletVPOAttributeCount()>0)
                        ) {
                            prfreeGoodsList.add(freeGoodGroups);
                        }
                    }
                }
            }


            if (prfreeGoodsList.size() > 0)
            {
                //check if the FreeGoodGroup already applied

                Integer alreadyApplied = 0;

                OUTER: for ( Integer appliedGoods : AppliedFreeGoodGroupIds){

                    for (FreeGoodGroups prGetFreeGoods : prfreeGoodsList){

                        if (appliedGoods.equals(prGetFreeGoods.getId())){
                            alreadyApplied++;
                            break OUTER;

                        }
                    }
                }

                if (alreadyApplied == 0)
                {
                    FreeGoodGroups Promo = new FreeGoodGroups(); // prFreeGood replace with FreeGoodGroups

//                    var bundles = prfreeGoodsList.Where(x => x.IsBundle == true); //if there exist bundle in the freegoodsList than only apply the single/First bundle offer and do not appy the remaining freegoods/Promos.
//                    if (bundles.size() > 0)
//                    {
//                        Promo = bundles.FirstOrDefault();
//                    }
//                    else
//                    {
//                        Promo = prfreeGoodsList.FirstOrDefault();
//                    }

                    if (prfreeGoodsList.size() > 0)
                        Promo = prfreeGoodsList.get(0);

                    List<FreeGoodOutputDTO> freeGoodOutputList = GetAvailableFreeGoods(Promo, ProductList, OutletId, 0);

                    freeGoodDTOList.addAll(freeGoodOutputList);
                    break;
                }
                else
                {
                    break;
                }
            }
        }

        return freeGoodDTOList;
    }


    //Get List of products which will be given free.
    private List<FreeGoodOutputDTO> GetAvailableFreeGoods(FreeGoodGroups freeGood, List<ProductQuantity> ProductList, int OutletId, int OrderId)
    {
        List<FreeGoodOutputDTO> FreeGoodOutputList = new ArrayList<>();
        int totalOrderedPromoQuantity = 0;
        if (freeGood.getTypeId() == (int) Enums.EnumFreeGoodsType.Inclusive)
        {
//            if (freeGood.IsBundle == false)
//            {


                FreeGoodOutputList = pricingDao.getFreeGoodGroupDetails(freeGood.getId()).subscribeOn(Schedulers.io()).blockingGet();
//                _freeGoodDetailRepository
//                        .GetListOf(x => x.FreeGoodGroupId == freeGood.FreeGoodGroupId && x.IsActive == true && x.IsDeleted == false)
//                                  .Select(x => new FreeGoodOutputDTO
//                {
//                    FreeGoodGroupId = x.FreeGoodGroupId,
//                            FreeGoodQuantity = x.FreeGoodQuantity,
//                            FreeGoodTypeId = freeGood.FreeGoodTypeId, //(int)EnumFreeGoodsType.Inclusive,
//                            MaximumFreeGoodQuantity = freeGood.MaximumQuantity,
//                            ProductSize = x.ProductDefinition.SizeForDisplay,
//                            IsDefault = x.ProductDefinition.IsDefault,
//                            DefinitionCode = x.ProductDefinition.Code,
//                            ProductName = x.Product.Name,
//                            ProductId = x.ProductId,
//                            ProductDefinitionId = x.ProductDefinitionId,
//                            ForEachQuantity = freeGood.ForEachQuantity,
//                            FreeGoodDetailId = x.Id,
//                            ProductCode = x.Product.Code,
//
//                }).ToList();

//            }
//            else if (freeGood.IsBundle == true)
//            {
//                FreeGoodOutputList = _freeGoodDetailRepository
//                        .GetListOf(x => x.FreeGoodGroupId == freeGood.FreeGoodGroupId && x.IsActive == true && x.IsDeleted == false)
//                                   .Select(x => new FreeGoodOutputDTO
//                {
//                    FreeGoodGroupId = x.FreeGoodGroupId,
//                            FreeGoodQuantity = x.FreeGoodQuantity,
//                            FreeGoodTypeId = freeGood.FreeGoodTypeId, //(int)EnumFreeGoodsType.Inclusive,
//                            MaximumFreeGoodQuantity = (int)x.MaximumQuantity,
//                            ProductSize = x.ProductDefinition.SizeForDisplay,
//                            IsDefault = x.ProductDefinition.IsDefault,
//                            DefinitionCode = x.ProductDefinition.Code,
//                            ProductName = x.Product.Name,
//                            ProductId = x.ProductId,
//                            ProductDefinitionId = x.ProductDefinitionId,
//                            ForEachQuantity = freeGood.ForEachQuantity,
//                            FreeGoodDetailId = x.Id,
//                            ProductCode = x.Product.Code,
//                }).ToList();
//            }

            for (ProductQuantity productQuantity : ProductList){

                for (FreeGoodOutputDTO freeGoodOutputDTO : FreeGoodOutputList){

                    if(productQuantity.getProductDefinitionId() == freeGoodOutputDTO.getProductDefinitionId()){
                        totalOrderedPromoQuantity = totalOrderedPromoQuantity + productQuantity.getQuantity();
                    }
                }
            }
//           / totalOrderedPromoQuantity = ProductList.Where(x => FreeGoodOutputList.Any(y => y.ProductDefinitionId == x.ProductDefinitionId)).Sum(s => s.Quantity);

        }
        else if (freeGood.getTypeId() == (int) Enums.EnumFreeGoodsType.Exclusive)
        {
            if (freeGood.getFreeQuantityTypeId() == (int) Enums.EnumFreeGoodsQuantityType.Primary)
            {
                FreeGoodOutputList = pricingDao.getFreeGoodExclusiveDetails(freeGood.getId()).subscribeOn(Schedulers.io()).blockingGet();

                for (FreeGoodOutputDTO freeGoodOutputDTO : FreeGoodOutputList){

                    freeGoodOutputDTO.setFreeGoodTypeId(freeGood.getTypeId());
                    freeGoodOutputDTO.setForEachQuantity(freeGood.getForEachQuantity());
                }
            }
            else if (freeGood.getFreeQuantityTypeId() == (int) Enums.EnumFreeGoodsQuantityType.Optional)
            {

                FreeGoodOutputList = pricingDao.getFreeGoodExclusiveDetails(freeGood.getId()).subscribeOn(Schedulers.io()).blockingGet();

                for (FreeGoodOutputDTO freeGoodOutputDTO : FreeGoodOutputList){

                    freeGoodOutputDTO.setFreeGoodTypeId(freeGood.getTypeId());
                    freeGoodOutputDTO.setForEachQuantity(freeGood.getForEachQuantity());
                }
            }

            List<Integer> promoBaseProducts = pricingDao.getPromoBaseProduct(freeGood.getId()).subscribeOn(Schedulers.io()).blockingGet();
//            totalOrderedPromoQuantity = ProductList.Where(x => promoBaseProducts.Any(y => y.ProductDefinitionId == x.ProductDefinitionId)).Sum(s => s.Quantity);

            for (ProductQuantity productQuantity : ProductList){

                for (Integer promo : promoBaseProducts){

                    if(productQuantity.getProductDefinitionId() == promo){
                        totalOrderedPromoQuantity = totalOrderedPromoQuantity + productQuantity.getQuantity();
                    }
                }
            }

        }
        if (FreeGoodOutputList != null && FreeGoodOutputList.size() > 0)
        {
            for (FreeGoodOutputDTO  item : FreeGoodOutputList)
            {
                item.setFreeQuantityTypeId(freeGood.getFreeQuantityTypeId());
                Integer alreadyAvailedFreeGoods = pricingDao.getAlreadyAvailedFreeGoods(item.getFreeGoodGroupId(), item.getFreeGoodDetailId(), item.getFreeGoodExclusiveId() ,  OutletId ).subscribeOn(Schedulers.io()).blockingGet();
                GetFinalFreeGoodQuantity(item, totalOrderedPromoQuantity, alreadyAvailedFreeGoods);
            }
        }
        return FreeGoodOutputList;
    }


    //Calculate the exact free Quantity of the product which will be given free after applying max limits and checking already availed products..
    private FreeGoodOutputDTO GetFinalFreeGoodQuantity(FreeGoodOutputDTO freegood, Integer totalOrderedPromoQuantity, Integer alreadyAvailedFreeGoods)
    {
        if (freegood.getForEachQuantity() > 0)
            freegood.setQualifiedFreeGoodQuantity((totalOrderedPromoQuantity / freegood.getForEachQuantity()) * freegood.getFreeGoodQuantity());
        freegood.setFinalFreeGoodsQuantity(freegood.getQualifiedFreeGoodQuantity());
        if (alreadyAvailedFreeGoods == null) { alreadyAvailedFreeGoods = 0; }


        Integer remainingMaxQuantity = freegood.getFinalFreeGoodsQuantity();
         if (freegood.getMaximumFreeGoodQuantity() != null && freegood.getMaximumFreeGoodQuantity() != 0)
        {
            remainingMaxQuantity = freegood.getMaximumFreeGoodQuantity() - (int)alreadyAvailedFreeGoods;
        }

        if (freegood.getFinalFreeGoodsQuantity() > remainingMaxQuantity)
        {
            freegood.setFinalFreeGoodsQuantity(remainingMaxQuantity);
            Message message = new Message();
            message.setMessageText( "The Order Qualified for the " +freegood.getQualifiedFreeGoodQuantity() +" items as FOC but you are getting your remaining Max. FOC for the promotion("+freegood.getFinalFreeGoodsQuantity() +").");

                //MessageText = "the Order Qualified for the "+freegood.QualifiedFreeGoodQuantity+ " but you are getting your remaining max freegoods for the promotion({})." //AppConstants.MsgPromoMaxLimitCrossed
            if (freegood.getMessages() == null)
            {
                freegood.setMessages(new ArrayList<>());
            }
            freegood.getMessages().add(message);
        }
        return freegood;
    }


}
