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
import com.optimus.eds.db.entities.CartonPriceBreakDown;
import com.optimus.eds.db.entities.OrderDetail;
import com.optimus.eds.db.entities.UnitPriceBreakDown;
import com.optimus.eds.db.entities.pricing.PriceAccessSequence;
import com.optimus.eds.db.entities.pricing.PriceConditionClass;
import com.optimus.eds.db.entities.pricing.PriceConditionDetail;
import com.optimus.eds.db.entities.pricing.PriceConditionEntities;
import com.optimus.eds.db.entities.pricing.PriceConditionScale;
import com.optimus.eds.db.entities.pricing.PriceConditionType;
import com.optimus.eds.db.entities.pricing.PricingArea;
import com.optimus.eds.db.entities.pricing_models.PriceConditionDetailsWithScale;
import com.optimus.eds.model.OrderResponseModel;
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

public class PricingManager {
    private String TAG = PricingManager.class.getSimpleName();
    private static PricingManager ourInstance = null;

    private final AppDatabase appDatabase;
    private final PricingDao pricingDao;
    private final PriceConditionEntitiesDao priceConditionEntitiesDao;

    public static PricingManager getInstance(Application application) {
        if (ourInstance == null)
            ourInstance = new PricingManager(application);
        return ourInstance;
    }

    private PricingManager(Application application) {

        appDatabase = AppDatabase.getDatabase(application);
        pricingDao = appDatabase.pricingDao();
        priceConditionEntitiesDao = appDatabase.priceConditionEntitiesDao();
    }


    //region "Public Functions"

    public PriceOutputDTO getOrderPrice(BigDecimal orderTotalAmount, int quantity, int outletId, int routeId, Integer distributionId) {
        PriceOutputDTO objPriceOutputDTO = new PriceOutputDTO();

        // AccessSequenceDTO appliedAccessSequence = new AccessSequenceDTO();
        //decimal totalPrice = 0; //input price for every condition class
        totalPrice = orderTotalAmount; //input price for every condition class
        isPriceFound = false;
        // @TODO  Remove pricingArea Loop if data comes from server with filtered pricing Areas
        List<PricingArea> pricingAreas = pricingDao.findPricingArea().subscribeOn(Schedulers.io()).blockingGet();
        List<PriceConditionClass> conditionClasses = pricingDao.findPriceConditionClasses(2).subscribeOn(Schedulers.io()).blockingGet();
        for (PricingArea priceArea : pricingAreas) {

            for (PriceConditionClass conditionClass : conditionClasses) {
                if (conditionClass.getPricingAreaId() != priceArea.getPricingAreaId())
                    continue;
                isPriceFound = false;
                List<PriceConditionType> conditionTypes = pricingDao.findPriceConditionTypes(conditionClass.getPriceConditionClassId()).subscribeOn(Schedulers.io()).blockingGet();
                for (PriceConditionType conditionType : conditionTypes) {

                    List<PriceConditionWithAccessSequence> priceConditions = pricingDao
                            .getPriceConditionAndAccessSequenceByTypeId(conditionType.getPriceConditionTypeId()).subscribeOn(Schedulers.io()).blockingGet();

                    Collections.sort(priceConditions, (o1, o2) -> o1.getOrder().compareTo(o2.getOrder()));
                    // for distribution check
//                    if (conditionClass.PricingType == (int)EnumPricingType.DistributionPricing)
//                    {
//                        priceConditions = priceConditions.Where(x => x.DistributionId == distributionId).ToList();
//                    }

                    for (PriceConditionWithAccessSequence priceCondition : priceConditions) {

                        PromoLimitDTO limit = GetPriceAgainstPriceConditionForInvoice(priceCondition.getPriceConditionId(), priceCondition.getSequenceCode(),
                                outletId, quantity, totalPrice, conditionType.getPriceScaleBasisId(), routeId, distributionId);

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

                            ItemAmountDTO blockPrice = CalculateBlockPrice(totalPrice.doubleValue(), limit.getUnitPrice().doubleValue(), quantity, conditionType.getPriceScaleBasisId(), conditionType.getOperationType(), conditionType.getCalculationType(), conditionType.getRoundingRule(), objSingleBlock.getLimitBy(), objSingleBlock.getMaximumLimit(), objSingleBlock.getAlreadyAvailed());

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
        }
        objPriceOutputDTO.setTotalPrice(BigDecimal.valueOf(Math.round(totalPrice.doubleValue())));
        return objPriceOutputDTO;
    }


    public Single<OrderResponseModel> calculatePriceBreakdown(OrderResponseModel orderModel) {

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
                    if (!orderDetail.type.equals("freegood"))
                         productQuantityDTOList.add(new ProductQuantity(orderDetail.getProductTempDefId(), orderDetail.getProductTempQuantity()));
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
                        priceOutputDTO = getOrderItemPrice(orderDetail.getMobileOrderDetailId(), orderModel.getOutletId()
                                , orderDetail.getProductTempDefId(), orderDetail.getProductTempQuantity()
                                , orderModel.getRouteId(), orderModel.getDistributionId() , combinedMaxLimitHolderDTOList);

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
    private PromoLimitDTO GetPriceAgainstPriceConditionForInvoice(int priceConditionId, String accessSequence, int outletId, int quantity, BigDecimal totalPrice, int scaleBasisId, int routeId, Integer distributionId) {
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

    private PromoLimitDTO GetPriceAgainstPriceCondition(int priceConditionId, String accessSequence, int outletId, int productDefinitionId, int quantity, BigDecimal totalPrice, int scaleBasisId, int routeId, Integer distributionId, Integer bundleId) {
        PromoLimitDTO promoLimitDTO = new PromoLimitDTO();
        if (accessSequence.equalsIgnoreCase(Enums.AccessSequenceCode.OUTLET_PRODUCT.toString())) {
            promoLimitDTO = GetPriceAgainstOutletProduct(priceConditionId, outletId, productDefinitionId, quantity, totalPrice, scaleBasisId, bundleId);
        } else if (accessSequence.equalsIgnoreCase(Enums.AccessSequenceCode.ROUTE_PRODUCT.toString()) || accessSequence.equalsIgnoreCase(Enums.AccessSequenceCode.REGION_PRODUCT.toString())) {
            promoLimitDTO = this.GetPriceAgainstRouteProduct(priceConditionId, routeId, productDefinitionId, quantity, totalPrice, scaleBasisId, bundleId);
        } else if (accessSequence.equalsIgnoreCase(Enums.AccessSequenceCode.DISTRIBUTION_PRODUCT.toString())) {
            promoLimitDTO = this.GetPriceAgainstDistributionProduct(priceConditionId, distributionId, productDefinitionId, quantity, totalPrice, scaleBasisId, bundleId);
        } else if (accessSequence.equalsIgnoreCase(Enums.AccessSequenceCode.PRODUCT.toString())) {
            promoLimitDTO = GetPriceAgainstProduct(priceConditionId, productDefinitionId, quantity, totalPrice, scaleBasisId, bundleId);
        } else if (accessSequence.equalsIgnoreCase(Enums.AccessSequenceCode.OUTLET.toString())) {
            promoLimitDTO = this.GetPriceAgainstOutlet(priceConditionId, outletId, quantity, totalPrice, scaleBasisId);
        } else if (accessSequence.equalsIgnoreCase(Enums.AccessSequenceCode.ROUTE.toString())) {
            promoLimitDTO = this.GetPriceAgainstRoute(priceConditionId, routeId, quantity, totalPrice, scaleBasisId);
        } else if (accessSequence.equalsIgnoreCase(Enums.AccessSequenceCode.DISTRIBUTION.toString())) {
            promoLimitDTO = this.GetPriceAgainstDistribution(priceConditionId, distributionId, quantity, totalPrice, scaleBasisId);
        }

        return promoLimitDTO;
    }

    private PromoLimitDTO GetPriceAgainstProduct(int priceConditionId, int productDefinitionId, int quantity, BigDecimal totalPrice, int scaleBasisId, Integer bundleId) {

        PromoLimitDTO maxLimitDTO = new PromoLimitDTO();
       /* PriceConditionDetailsWithScale priceConditionDetailsWithScale = bundleId==null?priceConditionEntitiesDao
                .findPriceConditionDetail(priceConditionId,productDefinitionId)
                .subscribeOn(Schedulers.io()).blockingGet():priceConditionEntitiesDao
                .findPriceConditionDetailWithBundle(priceConditionId,productDefinitionId,bundleId)
                .subscribeOn(Schedulers.io()).blockingGet();*/

        PriceConditionDetailsWithScale priceConditionDetailsWithScale = priceConditionEntitiesDao
                .findPriceConditionDetailWithBundle(priceConditionId, productDefinitionId, bundleId)
                .subscribeOn(Schedulers.io()).blockingGet();
        if (priceConditionDetailsWithScale == null)
            return null;
        PriceConditionDetail detail = priceConditionDetailsWithScale.getPriceConditionDetail();
        if (detail != null) {
            maxLimitDTO.setPriceConditionDetailId(detail.getPriceConditionDetailId());
            maxLimitDTO.setLimitBy(detail.getLimitBy());
            maxLimitDTO.setMaximumLimit(detail.getMaximumLimit());

            if (priceConditionDetailsWithScale.getPriceConditionScaleList().size() < 1) {
                maxLimitDTO.setUnitPrice(detail.getAmount());
            } else if (priceConditionDetailsWithScale.getPriceConditionScaleList().size() > 0) {
                BigDecimal unitPrice = GetScaledAmount(priceConditionDetailsWithScale.getPriceConditionScaleList(), detail.getPriceConditionDetailId(), scaleBasisId, quantity, totalPrice);
                maxLimitDTO.setUnitPrice(unitPrice);
            }
        }
        return maxLimitDTO;

    }

    private PromoLimitDTO GetPriceAgainstOutletProduct(int priceConditionId, int outletId, int productDefinitionId, int quantity, BigDecimal totalPrice, int scaleBasisId, Integer bundleId) {
        PromoLimitDTO maxLimitDTO = new PromoLimitDTO();
        PriceConditionDetailsWithScale priceConditionDetailsWithScale = priceConditionEntitiesDao.findPriceConditionEntityOutlet(priceConditionId, outletId)
                .flatMap(priceConditionEntities -> getPriceConditionDetailObservable(priceConditionEntities, priceConditionId, productDefinitionId, bundleId))
                .subscribeOn(Schedulers.io()).blockingGet();
        if (priceConditionDetailsWithScale == null)
            return null;
        PriceConditionDetail detail = priceConditionDetailsWithScale.getPriceConditionDetail();
        if (detail != null) {
            maxLimitDTO.setPriceConditionDetailId(detail.getPriceConditionDetailId());
            maxLimitDTO.setLimitBy(detail.getLimitBy());
            maxLimitDTO.setMaximumLimit(detail.getMaximumLimit());
            if (priceConditionDetailsWithScale.getPriceConditionScaleList().size() < 1) {
                maxLimitDTO.setUnitPrice(detail.getAmount());
            } else if (priceConditionDetailsWithScale.getPriceConditionScaleList().size() > 0) {
                BigDecimal unitPrice = GetScaledAmount(priceConditionDetailsWithScale.getPriceConditionScaleList(), detail.getPriceConditionDetailId(), scaleBasisId, quantity, totalPrice);
                maxLimitDTO.setUnitPrice(unitPrice);
            }
        }
        return maxLimitDTO;

    }

    private PromoLimitDTO GetPriceAgainstRouteProduct(int priceConditionId, int routeId, int productDefinitionId, int quantity, BigDecimal totalPrice, int scaleBasisId, Integer bundleId) {

        PromoLimitDTO maxLimitDTO = new PromoLimitDTO();

        PriceConditionDetailsWithScale priceConditionDetail = priceConditionEntitiesDao.findPriceConditionEntityRoute(priceConditionId, routeId)
                .flatMap(priceConditionEntities -> getPriceConditionDetailObservable(priceConditionEntities, priceConditionId, productDefinitionId, bundleId))
                .subscribeOn(Schedulers.io()).blockingGet();

        if (priceConditionDetail != null) {
            PriceConditionDetail detail = priceConditionDetail.getPriceConditionDetail();
            maxLimitDTO.setPriceConditionDetailId(detail.getPriceConditionDetailId());
            maxLimitDTO.setLimitBy(detail.getLimitBy());
            maxLimitDTO.setMaximumLimit(detail.getMaximumLimit());
            if (priceConditionDetail.getPriceConditionScaleList().size() < 1) {
                maxLimitDTO.setUnitPrice(detail.getAmount());
            } else if (priceConditionDetail.getPriceConditionScaleList().size() > 0) {
                BigDecimal unitPrice = GetScaledAmount(priceConditionDetail.getPriceConditionScaleList(), detail.getPriceConditionDetailId(), scaleBasisId, quantity, totalPrice);
                maxLimitDTO.setUnitPrice(unitPrice);
            }
        }


        return maxLimitDTO;
    }

    private PromoLimitDTO GetPriceAgainstDistributionProduct(int priceConditionId, Integer distributionId, int productDefinitionId, int quantity, BigDecimal totalPrice, int scaleBasisId, Integer bundleId) {


        PromoLimitDTO maxLimitDTO = new PromoLimitDTO();

        PriceConditionDetailsWithScale priceConditionDetail = priceConditionEntitiesDao.findPriceConditionEntityDistribution(priceConditionId, distributionId)
                .flatMap(priceConditionEntities -> getPriceConditionDetailObservable(priceConditionEntities, priceConditionId, productDefinitionId, bundleId))
                .subscribeOn(Schedulers.io()).blockingGet();
        if (priceConditionDetail != null) {
            PriceConditionDetail detail = priceConditionDetail.getPriceConditionDetail();
            maxLimitDTO.setPriceConditionDetailId(detail.getPriceConditionDetailId());
            maxLimitDTO.setLimitBy(detail.getLimitBy());
            maxLimitDTO.setMaximumLimit(detail.getMaximumLimit());
            if (priceConditionDetail.getPriceConditionScaleList().size() < 1) {
                maxLimitDTO.setUnitPrice(detail.getAmount());
            } else if (priceConditionDetail.getPriceConditionScaleList().size() > 0) {
                BigDecimal unitPrice = GetScaledAmount(priceConditionDetail.getPriceConditionScaleList(), detail.getPriceConditionDetailId(), scaleBasisId, quantity, totalPrice);
                maxLimitDTO.setUnitPrice(unitPrice);
            }
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
                BigDecimal unitPrice = GetScaledAmount(priceConditionDetail.getPriceConditionScaleList(), detail.getPriceConditionDetailId(), scaleBasisId, quantity, totalPrice);
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
                BigDecimal unitPrice = GetScaledAmount(priceConditionDetail.getPriceConditionScaleList(), detail.getPriceConditionDetailId(), scaleBasisId, quantity, totalPrice);
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
                BigDecimal unitPrice = GetScaledAmount(priceConditionDetail.getPriceConditionScaleList(), detail.getPriceConditionDetailId(), scaleBasisId, quantity, totalPrice);
                maxLimitDTO.setUnitPrice(unitPrice);
            }
        }

        return maxLimitDTO;
    }


    private Maybe<PriceConditionDetailsWithScale> getPriceConditionDetailObservable(PriceConditionEntities priceConditionEntity
            , int priceConditionId, int productDefinitionId, Integer bundleId) {
        if (priceConditionEntity == null)
            return null;

        return priceConditionEntitiesDao.findPriceConditionDetailWithBundle(priceConditionId,
                productDefinitionId, bundleId)
                .subscribeOn(Schedulers.single());

    }


    private BigDecimal GetScaledAmount(List<PriceConditionScale> scaleList, int priceConditionDetailId, int scaleBasisId, int quantity, BigDecimal totalPrice) {
        BigDecimal returnAmount = BigDecimal.ZERO;
        if (scaleBasisId == Enums.ScaleBasis.Quantity || scaleBasisId == Enums.ScaleBasis.Total_Quantity) {
            Collections.sort(scaleList, (o1, o2) -> o2.getFrom().compareTo(o1.getFrom()));
            for (PriceConditionScale conditionScale : scaleList) {

                if (conditionScale.getPriceConditionDetailId() == priceConditionDetailId && conditionScale.getFrom() <= quantity) {
                    returnAmount = conditionScale.getAmount();
                    break;
                }
            }

        } else if (scaleBasisId == Enums.ScaleBasis.Value) {
            Collections.sort(scaleList, (o1, o2) -> o2.getAmount().compareTo(o1.getAmount()));
            for (PriceConditionScale conditionScale : scaleList) {

                if (conditionScale.getPriceConditionDetailId() == priceConditionDetailId && conditionScale.getFrom() <= totalPrice.doubleValue()) {
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
            int mobileOrderDetailId,
            int outletId, int productDefinitionId, int quantity, int routeId, Integer distributionId , List<CombinedMaxLimitHolderDTO> combinedMaxLimitHolderDTOList) {
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
                if (bundlesToApply.isEmpty())
                    priceConditions = pricingDao
                            .getPriceConditionAndAccessSequenceByTypeId(conditionType.getPriceConditionTypeId()).subscribeOn(Schedulers.io()).blockingGet();
                else {
                    priceConditions = pricingDao
                            .getPriceConditionAndAccessSequenceByTypeIdWithBundle(conditionType.getPriceConditionTypeId(), bundlesToApply).subscribeOn(Schedulers.io()).blockingGet();
                }

                Collections.sort(priceConditions, (o1, o2) -> o1.getOrder().compareTo(o2.getOrder()));
                for (PriceConditionWithAccessSequence prAccSeqDetail : priceConditions) {

                    PromoLimitDTO limitDTO = GetPriceAgainstPriceCondition(prAccSeqDetail.getPriceConditionId(), prAccSeqDetail.getSequenceCode(),
                            outletId, productDefinitionId, quantity, totalPrice, conditionType.getPriceScaleBasisId(), routeId,
                            distributionId, prAccSeqDetail.getBundleId());


                    if (limitDTO != null && limitDTO.getUnitPrice().doubleValue() > -1) {
                        isPriceFound = true;
                        UnitPriceBreakDown objSingleBlock = new UnitPriceBreakDown();

                        objSingleBlock.setPriceConditionDetailId(limitDTO.getPriceConditionDetailId());
                        objSingleBlock.setOrderDetailId(mobileOrderDetailId);
                        if (limitDTO.getLimitBy() != null) {
                            objSingleBlock.setMaximumLimit(limitDTO.getMaximumLimit().doubleValue());
                            objSingleBlock.setLimitBy(limitDTO.getLimitBy());

                        /*   var objAlreadyAvailed = _outletAvailedPromotionDataHandler.GetAlreadyAvailedValue(objSingleBlock.getPriceConditionDetailId());
                            if (objAlreadyAvailed != null)
                            {
                                if (objSingleBlock.getLimitBy() == Enums.LimitBy.Amount)
                                {
                                    objSingleBlock.setAlreadyAvailed(objAlreadyAvailed.Amount);
                                }
                                else
                                {
                                    objSingleBlock.setAlreadyAvailed(objAlreadyAvailed.Quantity);
                                }
                            }*/
                        }

                        ItemAmountDTO blockPrice = CalculateBlockPrice(totalPrice.doubleValue(), limitDTO.getUnitPrice().doubleValue(), quantity, conditionType.getPriceScaleBasisId(), conditionType.getOperationType(), conditionType.getCalculationType(), conditionType.getRoundingRule(), objSingleBlock.getLimitBy(), objSingleBlock.getMaximumLimit(), objSingleBlock.getAlreadyAvailed());
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

    private ItemAmountDTO CalculateBlockPrice(Double inputAmount, Double amount, int quantity, int scaleBasisId, int operationType, int calculationType, int roundingRule, Integer limitBy, Double maxLimit, Double alreadyAvailed) {

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
                objReturnPrice.TotalPrice = inputAmount + blockPrice;
            } else if (operationType == Enums.OperationType.Minus) {
                objReturnPrice.TotalPrice = inputAmount - blockPrice;
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
                objReturnPrice.TotalPrice = inputAmount + actualValue;
            } else if (operationType == Enums.OperationType.Minus) {
                objReturnPrice.TotalPrice = inputAmount - actualValue;
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

    private double getRemainingBlockPrice(double amount, double maxLimit, Double alreadyAvailed) {
        if (alreadyAvailed == null) {
            alreadyAvailed = 0.0;
        }
        double remainingAmount = maxLimit - alreadyAvailed;
        if (remainingAmount < amount) {
            amount = remainingAmount;
        }
        return amount;
    }


    // region "Free Goods"

    public List<FreeGoodOutputDTO> GetFreeGoods(int outletId, int routeId, int channelId, int distributionId, int productDefinitionId, Date OrderDate, List<ProductQuantity> productList, List<Integer> appliedFreeGoodGroupIds, int orderId) {
        String predicate = "PricingTypeId = 2";
        List<PriceAccessSequence> accessSequenceList = pricingDao.getAccessSequence().blockingGet();
        List<FreeGoodOutputDTO> freeGoodDTOList = new ArrayList<>();
        List<prGetFreeGoods> prfreeGoodsList = new ArrayList<>();
        List<ProductQuantity> udtProductList = new ArrayList<>(productList);


     /*   for (PriceAccessSequence sequence : accessSequenceList) {
            if (sequence.getSequenceCode().equalsIgnoreCase(Enums.AccessSequenceCode.OUTLET_PRODUCT.toString())) {
                prfreeGoodsList = _freeGoodMasterRepository.prGetFreeGoods(outletId, 0, 0, 0, productDefinitionId, OrderDate, udtProductList, sequence.getPriceAccessSequenceId());

            } else if (sequence.getSequenceCode().equalsIgnoreCase(Enums.AccessSequenceCode.ROUTE_PRODUCT.toString())) {
                prfreeGoodsList = _freeGoodMasterRepository.prGetFreeGoods(0, routeId, 0, 0, productDefinitionId, OrderDate, udtProductList, sequence.getPriceAccessSequenceId());
            } else if (sequence.getSequenceCode().equalsIgnoreCase(Enums.AccessSequenceCode.DISTRIBUTION_PRODUCT.toString())) {
                prfreeGoodsList = _freeGoodMasterRepository.prGetFreeGoods(0, 0, 0, distributionId, productDefinitionId, OrderDate, udtProductList, sequence.getPriceAccessSequenceId());
            } else if (sequence.getSequenceCode().equalsIgnoreCase(Enums.AccessSequenceCode.PRODUCT.toString())) {
                prfreeGoodsList = _freeGoodMasterRepository.prGetFreeGoods(0, 0, 0, 0, productDefinitionId, OrderDate, udtProductList, sequence.getPriceAccessSequenceId());
            }
            if (prfreeGoodsList.size() > 0) {
                //check if the FreeGoodGroup already applied
                var alreadyApplied = prfreeGoodsList.Where(x = > x.FreeGoodGroupId == appliedFreeGoodGroupIds.Where(y = > y == x.FreeGoodGroupId).
                FirstOrDefault()).Count();

                if (alreadyApplied == 0) {
                    prGetFreeGoods promo = new prGetFreeGoods();

                    var bundles = prfreeGoodsList.Where(x = > x.IsBundle == true)
                    ; //if there exist bundle in the freegoodsList than only apply the single/First bundle offer and do not apply the remaining freegoods/Promos.
                    if (bundles.Count() > 0) {
                        promo = bundles.FirstOrDefault();
                    } else {
                        promo = prfreeGoodsList.FirstOrDefault();
                    }


                    var freeGoodOutputList = this.GetAvailableFreeGoods(promo, productList, outletId, orderId);

                    freeGoodDTOList.AddRange(freeGoodOutputList);
                    break;
                } else {
                    break;
                }
            }
        }
*/
        return freeGoodDTOList;
    }


}
